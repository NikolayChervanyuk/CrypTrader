import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../_core/services/auth.service';
import {Validator} from '../../_core/utils/Validator';
import {ApiResponse} from '../../_core/utils/ApiResponse';
import {LoginComponent} from '../login/login.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, NgIf, RouterLink, NgOptimizedImage],
  templateUrl: './register.component.html',
  styleUrl: '../auth.component.scss'
})
export class RegisterComponent {
  email = '';
  username = '';
  password = '';
  confirmPassword = '';
  errorMessage = '';

  constructor(private loginComponent: LoginComponent,
              private authService: AuthService,
              private router: Router
  ) {
  }

  register() {
    console.log("registering");

    const message = this.invalidFieldsMessage();
    if (message !== '') {
      this.errorMessage = message;
      return;
    }
    // console.log("email" + this.email);
    // console.log("username " + this.username);
    // console.log("password " + this.password);
    // console.log("conf_password " + this.confirmPassword);

    this.authService.register({email: this.email, username: this.username, password: this.password}).subscribe({
      next: (regResponse: ApiResponse) => {
        console.log('data = ' + regResponse.data);
        console.log('errorMsg = ' + regResponse.errorMessage);
        let registerResponse: boolean = regResponse.data;
        if (registerResponse) {
          this.loginComponent.loginWithCredentials(this.email, this.password) // TODO: will work?
          // this.authService.login({identifier: this.email, password: this.password}).subscribe({
          //   next: (logResponse: ApiResponse) => {
          //     if (logResponse.errorMessage) throw error(logResponse.errorMessage)
          //     let loginResponse: LoginResponse = logResponse.content;
          //     this.authService.setTokens(
          //       loginResponse.accessToken,
          //       loginResponse.refreshToken,
          //     )
          //     this.router.navigate(['/home']).then();
          //   },
          //   error: (err: string) => {
          //     this.errorMessage = err
          //   }
          // });
        } else throw new Error(regResponse.errorMessage || 'Registration failed. Please try again.');
      },
      error: () => {
        this.errorMessage = 'Registration failed. Please try again.';
      }
    });
  }

  private invalidFieldsMessage(): string {
    if (this.password === '' || this.confirmPassword === '') {
      return "Passwords can't be empty.";
    }

    if (this.password !== this.confirmPassword) {
      return "Passwords don't match.";

    }

    if (!Validator.isValidPassword(this.password)) {
      return "Passwords should be at least 4 characters long.";
    }

    if (!Validator.isValidUsername(this.username)) {
      return "Username should contain only letters and numbers.";
    }

    if (!Validator.isValidEmail(this.email)) {
      return "Invalid email address.";
    }
    return '';
  }
}
