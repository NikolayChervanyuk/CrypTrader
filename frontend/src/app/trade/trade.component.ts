import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TradeType} from '../_core/utils/TradeType';
import {NgSwitch, NgSwitchCase} from '@angular/common';
import {SellComponent} from './sell/sell.component';
import {BuyComponent} from './buy/buy.component';

@Component({
  selector: 'app-trade',
  imports: [
    NgSwitchCase,
    NgSwitch,
    SellComponent,
    BuyComponent
  ],
  templateUrl: './trade.component.html',
  styleUrl: './trade.component.scss'
})
export class TradeComponent implements OnInit {
  protected tradeType: TradeType | undefined;
  protected readonly TradeType = TradeType;


  protected symbol: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.initTradePage(this.route.snapshot.queryParams['type'])

  }

  private initTradePage(type: string) {
    console.log("Query param: trade type is " + type);
    if (type == null) {
      this.router.navigate(['/home']);
      return;
    }


    this.tradeType = TradeType[type.toUpperCase() as keyof typeof TradeType];
    console.log("this.tradeType is " + this.tradeType);
    if (this.tradeType != TradeType.BUY
      && this.tradeType != TradeType.SELL) {
      this.router.navigate(['/home']);
      return;
    }

    let symbol = this.route.snapshot.url.pop()?.toString()
    console.log("Symbol is " + symbol);
    if (symbol == null) {
      this.router.navigate(['/home']);
    }
    this.symbol = symbol;
  }
}
