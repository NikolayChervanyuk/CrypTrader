package com.nch.cryptrader.service;

import com.nch.cryptrader.entity.Trade;
import com.nch.cryptrader.entity.UsersAssets;
import com.nch.cryptrader.model.UserHistoryTradeModel;
import com.nch.cryptrader.entity.AppUser;
import com.nch.cryptrader.model.AppUserCredentialsModel;
import com.nch.cryptrader.model.AppUserModel;
import com.nch.cryptrader.model.UserAssetModel;
import com.nch.cryptrader.repository.AssetRepository;
import com.nch.cryptrader.repository.TradeRepository;
import com.nch.cryptrader.repository.UserRepository;
import com.nch.cryptrader.repository.UsersAssetsRepository;
import com.nch.cryptrader.state.TopCurrencies;
import com.nch.cryptrader.util.AuthPrincipalProvider;
import com.nch.cryptrader.util.IdentifierType;
import com.nch.cryptrader.util.TradeType;
import com.nch.cryptrader.view.AppUserCredentialsView;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.nch.cryptrader.util.IdentifierType.*;


@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UsersAssetsRepository usersAssetsRepository;
    private final AssetRepository assetRepository;
    private final TopCurrencies topCurrencies;
    private final TradeRepository tradeRepository;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;

    public Mono<AppUserCredentialsModel> getUserCredentialsByIdentifier(
            @NotNull final String identifier,
            @NotNull final String password
    ) {
        Mono<AppUserCredentialsView> foundUser = switch (getIdentifierType(identifier)) {
            case EMAIL -> userRepository.getUserCredentialsByEmail(identifier.toLowerCase());
            case USERNAME -> userRepository.getUserCredentialsByUsername(identifier.toLowerCase());
            case UNDEFINED -> Mono.empty();
        };
        return foundUser
                .flatMap(user ->
                        passwordEncoder.matches(password, user.getPassword()) ?
                                Mono.just(user) : Mono.empty()
                )
                .mapNotNull(userCredentialsView ->
                        conversionService.convert(userCredentialsView, AppUserCredentialsModel.class)
                );
    }

    public Mono<UserPersistenceStatus> saveUser(@NotNull final AppUserModel userModel) {
        userModel.setBalance(new BigDecimal(100_000));
        return userRepository.getUserCredentialsByUsername(userModel.getUsername())
                .map(userView -> UserPersistenceStatus.USERNAME_EXISTS)
                .switchIfEmpty(userRepository.getUserCredentialsByEmail(userModel.getEmail())
                        .map(userView -> UserPersistenceStatus.EMAIL_EXISTS)
                        .switchIfEmpty(
                                userRepository.save(
                                        Objects.requireNonNull(conversionService.convert(userModel, AppUser.class))
                                ).thenReturn(UserPersistenceStatus.SUCCESS)
                        )
                ).onErrorReturn(UserPersistenceStatus.INTERNAL_ERROR);
    }

    public Mono<AppUserCredentialsModel> getUserDetails() {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userRepository::getById)
                .mapNotNull(userCredentials ->
                        conversionService.convert(userCredentials, AppUserCredentialsModel.class)
                );
    }

    public Mono<Boolean> issueTokenRevocation() {
        return AuthPrincipalProvider.getAuthenticatedUserMono()
                .flatMap(user -> userRepository.issueTokenRevocationForUser(user.getUsername()));
    }

    public Flux<UserAssetModel> getUserAssets() {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flux()
                .flatMap(usersAssetsRepository::getByUserId)
                .flatMap(usersAssets ->
                        assetRepository.getById(usersAssets.getAssetId()).map(asset ->
                                UserAssetModel.builder()
                                        .symbol(asset.getSymbol())
                                        .totalProfit(usersAssets.getTotalProfit().doubleValue())
                                        .quantity(usersAssets.getQuantity().doubleValue())
                                        .currentPrice(topCurrencies
                                                .getAssets()
                                                .get(asset.getSymbol())
                                                .getPrice()
                                                .doubleValue()).build()
                        )
                );
    }

    public Flux<UserHistoryTradeModel> getUserTradeHistory() {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flux()
                .flatMap(tradeRepository::getByUserId)
                .map(trade -> new UserHistoryTradeModel(
                        trade.getTradeType(),
                        trade.getSymbol(),
                        trade.getQuantity(),
                        trade.getTotalPrice().doubleValue(),
                        trade.getCreationDate()
                ));
    }

    //TODO: Bloated - Split parts of the chain to other methods
    @Transactional
    public Mono<Boolean> buyAsset(String symbol, double quantity) {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userRepository::getAppUserById)
                .flatMap(user -> {
                    final var quantityDec = new BigDecimal(quantity);
                    final var totalPrice = topCurrencies.getAssets().get(symbol).getPrice().multiply(quantityDec);
                    if (user.getBalance().compareTo(totalPrice) < 0) {
                        log.debug("Insufficient funds");
                        return Mono.just(false);
                    }
                    user.setBalance(user.getBalance().subtract(totalPrice));
                    return userRepository.save(user)
                            .flatMap(usr -> tradeRepository.save(
                                    new Trade(user.getId(), symbol, TradeType.BUY, quantity, totalPrice)
                            ))
                            .then(Mono.defer(() -> {
                                return assetRepository.getBySymbol(symbol).flatMap(asset -> usersAssetsRepository
                                        .getByUserIdAndAssetId(user.getId(), asset.getId())
                                        .flatMap(usersAssets -> {
                                            usersAssets.setQuantity(usersAssets.getQuantity().add(quantityDec));
                                            usersAssets.setTotalProfit(usersAssets.getTotalProfit().subtract(totalPrice));
                                            log.debug("Asset bought successfully");
                                            return usersAssetsRepository.save(usersAssets)
                                                    .thenReturn(true);
                                        })
                                        .switchIfEmpty(usersAssetsRepository.save(
                                                        new UsersAssets(user.getId(), asset.getId(), quantityDec, totalPrice.negate())
                                                ).thenReturn(true)
                                        )
                                );
                            }));
                });
    }

    @Transactional
    public Mono<Boolean> sellAsset(String symbol, double quantity) {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userRepository::getAppUserById)
                .flatMap(user -> {
                    final var quantityDec = new BigDecimal(quantity);

                    return assetRepository.getBySymbol(symbol).flatMap(asset -> usersAssetsRepository
                            .getByUserIdAndAssetId(user.getId(), asset.getId())
                            .flatMap(usersAssets -> {
                                if (quantityDec.compareTo(usersAssets.getQuantity()) > 0) {
                                    log.debug("Not enough quantity");
                                    return Mono.just(false);
                                }
                                final var totalPrice = topCurrencies.getAssets().get(symbol).getPrice().multiply(quantityDec);
                                return tradeRepository.save(
                                                new Trade(user.getId(), symbol, TradeType.SELL, quantity, totalPrice)
                                        )
                                        .then(Mono.defer(() -> {
                                            user.setBalance(user.getBalance().add(totalPrice));
                                            return userRepository.save(user);
                                        }))
                                        .then(Mono.defer(() -> {
                                            if (usersAssets.getQuantity().compareTo(quantityDec) == 0) {
                                                return usersAssetsRepository.delete(usersAssets);
                                            }
                                            usersAssets.setQuantity(usersAssets.getQuantity().subtract(quantityDec));
                                            usersAssets.setTotalProfit(usersAssets.getTotalProfit().add(totalPrice));
                                            return usersAssetsRepository.save(usersAssets).then();
                                        }))
                                        .thenReturn(true);
                            }));
                });
    }

    @Transactional
    public Mono<Boolean> resetAccount() {
        return AuthPrincipalProvider.getAuthenticatedUserUUIDMono()
                .flatMap(userRepository::getAppUserById)
                .flatMap(user -> {
                    user.setBalance(BigDecimal.valueOf(100_000));
                    return userRepository.save(user)
                            .flatMap(appUser -> usersAssetsRepository.deleteByUserId(user.getId())
                                    .flatMap(b -> tradeRepository.deleteByUserId(user.getId())));
                })
                .thenReturn(true)
                .onErrorReturn(false);
    }

    @Getter
    public enum UserPersistenceStatus {
        USERNAME_EXISTS("Username already exists"),
        EMAIL_EXISTS("Email already exists"),
        INTERNAL_ERROR("Service unavailable"),
        SUCCESS("Registration successful");

        UserPersistenceStatus(final String statusMessage) {
            this.statusMessage = statusMessage;
        }

        private final String statusMessage;

    }

    private IdentifierType getIdentifierType(@NotNull String identifier) {
        if (identifier.contains(" ")) {
            return UNDEFINED;
        }
        if (isValidEmail(identifier)) {
            return EMAIL;
        }
        if (isValidUsername(identifier)) {
            return USERNAME;
        }
        return UNDEFINED;
    }

    private boolean isValidEmail(@NotNull String identifier) {
        Pattern pattern = Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                        "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
                Pattern.CASE_INSENSITIVE
        );
        return pattern.matcher(identifier).matches();
    }

    private boolean isValidUsername(@NotNull String identifier) {
        if (identifier.length() > 40) return false;
        if (identifier.charAt(0) == '.' ||
                identifier.charAt(identifier.length() - 1) == '.'
        ) {
            return false;
        } //Cant start or end with a comma

        boolean hasLetter = false;
        boolean isPrevSeparator = false;
        final String str = identifier.toLowerCase();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.' || str.charAt(i) == '_') {
                if (isPrevSeparator) return false;
                isPrevSeparator = true;
                continue;
            }
            if (isLetter(str.charAt(i)) || isDigit(str.charAt(i))) {
                hasLetter = true;
                isPrevSeparator = false;
                continue;
            }
            return false;
        }
        return hasLetter;
    }

    private boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z');
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
