import {Component, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {TradeHistoryComponent} from './components/trade-history/trade-history.component';
import {AssetsComponent} from './components/assets/assets.component';
import {UserService} from '../_core/services/user.service';
import {ApiResponse} from '../_core/utils/ApiResponse';
import {UserDetailsResponse} from '../_core/interfaces/reqresp/user/UserDetailsResponse';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, AssetsComponent, TradeHistoryComponent, NgOptimizedImage],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss'
})
export class AccountComponent implements OnInit {
  username: string = '';
  email: string = '';
  selectedTab: 'assets' | 'history' = 'assets';

  constructor(
    private route: ActivatedRoute,
    private userService: UserService) {
  }

  ngOnInit() {
    this.initUserDetails()
  }

  resetAccount() {
    console.log('reset account');
    this.userService.resetAccount().subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        if (resp.data === false) throw Error('Failed to reset account')
        window.location.reload();
      }, error: (err) => {
        console.log(err)
      }
    });
  }

  switchTab(tab: 'assets' | 'history') {
    this.selectedTab = tab;
  }

  private initUserDetails() {
    this.userService.getUserDetails().subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        let userDetails = resp.data as UserDetailsResponse;
        this.username = userDetails.username;
        this.email = userDetails.email;
      }, error: (err) => {
        console.log(err);
      }
    })
  }
}
