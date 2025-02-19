import {Component, OnDestroy, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {WebSocketService} from '../_core/services/web-socket.service';
import {Router} from '@angular/router';
import {TradeType} from '../_core/utils/TradeType';
import {CryptoCurrency} from '../_core/interfaces/reqresp/home/CryptoCurrency';
import {AssetsService} from '../_core/services/assets.service';
import {ApiResponse} from '../_core/utils/ApiResponse';
import {AssetUpdateMessage} from '../_core/interfaces/ws-messages/AssetUpdateMessage';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, OnDestroy {
  assets: CryptoCurrency[] = [];
  selectedTab: 'top20' | 'tradeVolume' = 'top20';
  private wsSubscription: any;

  constructor(
    private webSocketService: WebSocketService,
    private assetsService: AssetsService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.initTop20Assets()
    this.updateAssetPrice()
  }

  switchTab(tab: 'top20' | 'tradeVolume') {
    this.selectedTab = tab;
  }

  buy(symbol: string) {
    this.router.navigate([`/trade/${symbol}`], {queryParams: {type: TradeType.BUY.toString()}});
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }

  private initTop20Assets() {
    this.assetsService.getTop20Assets().subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        this.assets = resp.data as CryptoCurrency[];
        this.assets.sort((a, b) =>
          a.price > b.price ? -1 : 1
        );
      }, error: (err) => {
        console.log(err);
      }
    })
  }

  private updateAssetPrice() {
    this.wsSubscription = this.webSocketService.listen().subscribe((data: AssetUpdateMessage) => {
      let updatedAsset = data.asset;
      console.log(`Price update for ${updatedAsset.symbol}: ${updatedAsset.price}`);
      let indexOfAssetToUpdate = this.assets
        .findIndex((asset) => asset.symbol == updatedAsset.symbol);
      this.assets[indexOfAssetToUpdate].price = updatedAsset.price
      this.assets[indexOfAssetToUpdate].updated = true;

      setTimeout(() => {
        this.assets[indexOfAssetToUpdate].updated = false;
      }, 1000);
    });
  }
}
