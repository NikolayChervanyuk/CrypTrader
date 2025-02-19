import {Component, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {CookieService} from 'ngx-cookie-service';
import {UserService} from '../../services/user.service';
import {ApiResponse} from '../../utils/ApiResponse';
import {UserDetailsResponse} from '../../interfaces/reqresp/user/UserDetailsResponse';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  balance: number = 0;
  isLoggedIn: boolean = false;
  username: string = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private cookieService: CookieService
  ) {
  }

  ngOnInit() {
    this.isLoggedIn = !!this.cookieService.get('access_token');

    if (this.isLoggedIn) {
      this.initUserDetails()
    }
  }

  goToHome() {
    this.router.navigate(['/home']);
  }

  goToLogin() {
    this.router.navigate(['/auth/login']);
  }

  goToAccount() {
    this.router.navigate([`/account/${this.username}`]);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  private initUserDetails() {
    this.userService.getUserDetails().subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw Error(resp.errorMessage)
        let userDetails = resp.data as UserDetailsResponse;
        this.username = userDetails.username;
        console.log('header Balance is: ' + userDetails.balance);
        this.balance = userDetails.balance;
      }, error: (err) => {
        console.log(err);
      }
    })
  }
}
