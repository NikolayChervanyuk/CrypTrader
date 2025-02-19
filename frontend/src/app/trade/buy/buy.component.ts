import {Component, Input, OnDestroy, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../../_core/services/web-socket.service';
import {AssetsService} from '../../_core/services/assets.service';
import {UserService} from '../../_core/services/user.service';
import {ApiResponse} from '../../_core/utils/ApiResponse';
import {UserDetailsResponse} from '../../_core/interfaces/reqresp/user/UserDetailsResponse';
import {AssetUpdateMessage} from '../../_core/interfaces/ws-messages/AssetUpdateMessage';
import {AssetResponse} from '../../_core/interfaces/reqresp/asset/AssetResponse';
import {UserAsset} from '../../account/components/assets/UserAsset';
import {BuyRequest} from '../../_core/interfaces/reqresp/user/BuyRequest';

@Component({
  selector: 'app-buy',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './buy.component.html',
  styleUrl: '../trade.component.scss'
})

export class BuyComponent implements OnInit, OnDestroy {
  @Input()
  symbol: string | undefined;
  marketPrice = signal(0);

  balance: number = 0.0;
  balanceAfterTrade = signal(+this.balance);

  amountCryptoToBuy: number = 0.0;
  amountCryptoCurrentlyOwned: number = 0.0;
  amountCryptoOwnedAfterTrade = signal(0);

  costUSD: number = 0.0;

  isInsufficientFunds: boolean = false;
  confirmTrade: boolean = false;
  protected readonly Number = Number;
  private wsSubscription: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private webSocketService: WebSocketService,
    private assetService: AssetsService,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.initAsset()
    this.initBalance()
    this.updateAssetPrice()
  }

  onAmountChanged() {
    if (this.amountCryptoToBuy < 0) return
    this.costUSD = this.amountCryptoToBuy * this.marketPrice();
    if (this.costUSD > this.balance) {
      this.amountCryptoOwnedAfterTrade.set(0);
      this.isInsufficientFunds = true;
      return;
    }
    this.isInsufficientFunds = false;
    this.balanceAfterTrade.set(+this.balance - (+this.costUSD));
    this.amountCryptoOwnedAfterTrade.set(+this.amountCryptoCurrentlyOwned + (+this.amountCryptoToBuy.valueOf()));
    console.log('amountCryptoOwnedAfterTrade: ', this.amountCryptoOwnedAfterTrade());
  }

  buy() {
    if (!this.confirmTrade ||
      this.isInsufficientFunds ||
      this.amountCryptoToBuy <= 0
    ) return;
    console.log(`Sending buy request for ${this.symbol} with amount ${this.amountCryptoToBuy} and cost ${this.costUSD}`)

    this.userService.buyCurrency(
      {symbol: this.symbol, quantity: this.amountCryptoToBuy} as BuyRequest
    ).subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        let status = resp.data as boolean;
        if (!status) throw Error('Buy request failed')

        console.log('Buy request successful')
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

  ngOnDestroy() {
    this.wsSubscription.unsubscribe();
  }

  private initAsset() {
    this.symbol ? this.assetService.getAsset(this.symbol).subscribe({
      next: (resp: ApiResponse) => {
        console.log('assets/sym: ', resp.data);
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
        this.amountCryptoCurrentlyOwned = +(resp.data as UserAsset[])[0].quantity
        this.amountCryptoOwnedAfterTrade.set(+this.amountCryptoCurrentlyOwned + (+this.amountCryptoToBuy));
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
      if (data.asset.symbol !== this.symbol) return
      this.onMarketPriceChange(+data.asset.price)
    })
  }

  private onMarketPriceChange(marketPrice: number) {
    this.marketPrice.set(marketPrice);
    this.costUSD = this.amountCryptoToBuy * marketPrice;
    if (this.costUSD > this.balance) {
      this.isInsufficientFunds = true;
      this.amountCryptoOwnedAfterTrade.set(0);
      return;
    } else this.isInsufficientFunds = false;
    this.balanceAfterTrade.set(+this.balance - (+this.costUSD));
    this.amountCryptoOwnedAfterTrade.set(+this.amountCryptoCurrentlyOwned + (+this.amountCryptoToBuy));
  }
}
