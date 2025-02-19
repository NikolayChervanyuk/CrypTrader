import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TradeHistory} from '../../../_core/interfaces/reqresp/user/TradeHistory';
import {UserService} from '../../../_core/services/user.service';

@Component({
  selector: 'app-trade-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trade-history.component.html',
  styleUrl: './trade-history.component.scss'
})
export class TradeHistoryComponent implements OnInit {
  tradeHistory: TradeHistory[] = [];

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.initTradeHistory();
  }

  getDateFromTimestamp(timestamp: number) {
    return new Date(timestamp * 1000);
  }

  private initTradeHistory() {
    this.userService.getTradeHistory().subscribe({
      next: (resp) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        this.tradeHistory = (resp.data as TradeHistory[]);
        this.tradeHistory.sort((a, b) => a > b ? -1 : 1);
      },
      error: (err) => {
        console.log(err);
      }
    })
  }
}
