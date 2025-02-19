import {Component, OnDestroy, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserAsset} from './UserAsset';
import {WebSocketService} from '../../../_core/services/web-socket.service';
import {Router} from '@angular/router';
import {TradeType} from '../../../_core/utils/TradeType';
import {AssetUpdateMessage} from '../../../_core/interfaces/ws-messages/AssetUpdateMessage';
import {UserService} from '../../../_core/services/user.service';
import {ApiResponse} from '../../../_core/utils/ApiResponse';

@Component({
  selector: 'app-assets',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './assets.component.html',
  styleUrl: './assets.component.scss'
})
export class AssetsComponent implements OnInit, OnDestroy {
  ownedAssets: UserAsset[] = [];
  private wsSubscription: any;

  constructor(
    private webSocketService: WebSocketService,
    private userService: UserService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.initAssets()
    this.updateAssetPrices()

  }

  sell(symbol: string) {
    this.router.navigate([`/trade/${symbol}`], {queryParams: {type: TradeType.SELL.toString()}});
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }

  private initAssets() {
    console.log("init assets");
    this.userService.getAssets().subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        this.ownedAssets = resp.data as UserAsset[];
        this.ownedAssets.forEach(asset => {
          console.log("Asset ", asset.symbol, asset.quantity);
          asset.totalProfit += asset.currentPrice * asset.quantity;
        })
        this.ownedAssets.sort((a, b) =>
          a.currentPrice > b.currentPrice ? -1 : 1
        );
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  private updateAssetPrices() {
    this.wsSubscription = this.webSocketService.listen().subscribe((data: AssetUpdateMessage) => {
      let updatedAsset = data.asset;
      let indexOfAssetToUpdate = this.ownedAssets
        .findIndex(asset => asset.symbol == updatedAsset.symbol);
      if (indexOfAssetToUpdate == -1) return;
      console.log(`Price update for ${updatedAsset.symbol}: ${updatedAsset.price}`);
      let priceDelta = updatedAsset.price - this.ownedAssets[indexOfAssetToUpdate].currentPrice;
      this.ownedAssets[indexOfAssetToUpdate].totalProfit += priceDelta
      this.ownedAssets[indexOfAssetToUpdate].currentPrice = updatedAsset.price
    });
  }
}
