import {Component, Input, OnDestroy, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {ApiResponse} from '../../_core/utils/ApiResponse';
import {AssetResponse} from '../../_core/interfaces/reqresp/asset/AssetResponse';
import {UserAsset} from '../../account/components/assets/UserAsset';
import {AssetsService} from '../../_core/services/assets.service';
import {UserService} from '../../_core/services/user.service';
import {UserDetailsResponse} from '../../_core/interfaces/reqresp/user/UserDetailsResponse';
import {AssetUpdateMessage} from '../../_core/interfaces/ws-messages/AssetUpdateMessage';
import {WebSocketService} from '../../_core/services/web-socket.service';
import {SellRequest} from '../../_core/interfaces/reqresp/user/SellRequest';

@Component({
  selector: 'app-sell',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sell.component.html',
  styleUrl: '../trade.component.scss'
})

//TODO
export class SellComponent implements OnInit, OnDestroy {
  @Input()
  symbol: string | undefined;
  marketPrice = signal(0);

  balance: number = 0;
  balanceAfterTrade = signal(this.balance);

  amountCryptoToSell: number = 0;
  amountCryptoCurrentlyOwned: number = 0;
  amountCryptoOwnedAfterTrade = signal(0); // Placeholder, should come from API

  amountUSDToBeReceived: number = 0;

  isInsufficientFunds: boolean = false;
  confirmTrade: boolean = false;
  protected readonly Number = Number;
  private wsSubscription: any;

  constructor(
    private router: Router,
    private assetService: AssetsService,
    private userService: UserService,
    private webSocketService: WebSocketService
  ) {
  }

  ngOnInit() {
    this.initAsset()
    this.initBalance()
    this.updateAssetPrice()
  }

  onAmountChanged() {
    if (this.amountCryptoToSell < 0) return
    this.amountUSDToBeReceived = this.amountCryptoToSell * this.marketPrice();
    if (this.amountCryptoToSell > this.amountCryptoCurrentlyOwned) {
      this.amountCryptoOwnedAfterTrade.set(0);
      this.isInsufficientFunds = true;
      return;
    }
    this.isInsufficientFunds = false;
    this.balanceAfterTrade.set(this.balance + this.amountUSDToBeReceived);
    this.amountCryptoOwnedAfterTrade.set(+this.amountCryptoCurrentlyOwned - (+this.amountCryptoToSell));
  }

  sell() { //TODO: Api call
    // if (!this.confirmTrade || this.amountCryptoToSell <= 0 || this.amountCryptoToSell > this.ownedAmount) return;
    // console.log(`Selling ${this.amountCryptoToSell} ${this.symbol} for $${this.amountUSDToBeReceived}`);
    if (!this.confirmTrade ||
      this.isInsufficientFunds ||
      this.amountCryptoToSell <= 0
    ) return;
    console.log(`Sending sell request for ${this.symbol} with ` +
      `amount ${this.amountCryptoToSell} and price ${this.amountUSDToBeReceived}`
    )

    this.userService.sellCurrency(
      {symbol: this.symbol, quantity: this.amountCryptoToSell} as SellRequest
    ).subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        let status = resp.data as boolean;
        if (!status) throw Error('Sell request failed')
        console.log('Sell request successful')
      },
      error: (err) => {
        console.log(err);
      },
      complete: () => {
        this.router.navigate(['/home']);
        window.location.reload();
      }
    })
  }

  ngOnDestroy(): void {
    this.wsSubscription.unsubscribe();
  }

  private initAsset() {
    this.symbol ? this.assetService.getAsset(this.symbol).subscribe({
      next: (resp: ApiResponse) => {
        let asset = resp.data as AssetResponse;
        this.marketPrice.set(asset.price);
      }, error: (err) => {
        console.log(err);
      }
    }) : null;

    this.symbol ? this.userService.getAsset(this.symbol).subscribe({
      next: (resp: ApiResponse) => {
        console.log('user/assets/sym: ', resp.data);
        let respAssetListLength = (resp.data as UserAsset[]).length;
        if (respAssetListLength == 0) return;
        let asset = (resp.data as UserAsset[])[0];
        this.amountCryptoCurrentlyOwned = !!asset ? asset.quantity : 0
        this.amountCryptoOwnedAfterTrade.set(this.amountCryptoCurrentlyOwned - this.amountCryptoToSell);
      }
    }) : null
  }

  private initBalance() {
    this.userService.getUserDetails().subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        this.balance = (resp.data as UserDetailsResponse).balance;
      }, error: (err) => {
        console.log(err);
      }
    })
  }

  private updateAssetPrice() {
    this.wsSubscription = this.webSocketService.listen().subscribe((data: AssetUpdateMessage) => {
      if(data.asset.symbol !== this.symbol) return
      this.onMarketPriceChange(data.asset.price)
    })
  }

  private onMarketPriceChange(marketPrice: number) {
    this.marketPrice.set(marketPrice);
    if (this.amountCryptoToSell > this.amountCryptoCurrentlyOwned) {
      this.isInsufficientFunds = true;
      this.amountUSDToBeReceived = 0;
      return;
    } else this.isInsufficientFunds = false;
    this.amountUSDToBeReceived = this.amountCryptoToSell * marketPrice;
    this.balanceAfterTrade.set(this.balance + this.amountUSDToBeReceived);
    this.amountCryptoOwnedAfterTrade.set(+this.amountCryptoCurrentlyOwned - (+this.amountCryptoToSell));
  }
}
