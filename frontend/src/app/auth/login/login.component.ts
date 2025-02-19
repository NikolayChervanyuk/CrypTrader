import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../_core/services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {ApiResponse} from '../../_core/utils/ApiResponse';
import {LoginResponse} from '../../_core/interfaces/reqresp/auth/LoginResponse';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, NgOptimizedImage],
  templateUrl: './login.component.html',
  styleUrl: '../auth.component.scss'
})
export class LoginComponent {
  identifier = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  protected login() {
    this.loginWithCredentials(this.identifier, this.password)
  }

  loginWithCredentials(identifier: string, password: string) {
    this.authService.login({identifier: identifier, password: password}).subscribe({
      next: (resp: ApiResponse) => {
        if (!!resp.errorMessage) throw new Error(resp.errorMessage)
        let loginResponse: LoginResponse = resp.data;
        this.authService.setTokens(
          loginResponse.accessToken,
          loginResponse.refreshToken,
        )
        this.router.navigate(['/home']).then();
      },
      error: (err: string) => {
        this.errorMessage = err
      }
    });
  }
}
