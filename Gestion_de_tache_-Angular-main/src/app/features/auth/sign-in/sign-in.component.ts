import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  providers: [AuthService, ToastrService],
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css'],
})
export class SignInComponent {
  authRequest = {
    email: '',
    password: '',
  };

  ngOnInit(): void {
    this.authRequest = {
      email: '',
      password: '',
    };
  }


  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  onLogin() {
    console.log('Auth Request:', this.authRequest);

    if (!this.authRequest.email || !this.authRequest.password) {
      this.toastr.warning('Veuillez remplir tous les champs.', 'Avertissement');
      return;
    }

    this.authService.signIn(this.authRequest).subscribe({
      next: (response: { jwt: string }) => {
        if (response && response.jwt) {
          this.toastr.success('Successfully logged in!', 'Success');
          console.log('Login Success:', response);

          const token = response.jwt;
          localStorage.setItem('token', token);

          const role = this.authService.getDecodedRole(token);
          console.log('Decoded Role:', role);

          if (role === 'ADMIN') {
            
            this.router.navigate(['/admin']);
          } else if (role === 'NORMAL') {
            
            this.router.navigate(['/user']);
          } else {
            this.toastr.error('Unknown role. Please contact support.', 'Error');
          }
        } else {
          this.toastr.error('Invalid server response. Please try again.', 'Error');
        }
      },
      error: (error: any) => {
        console.error('Login Error:', error);

        if (error.status === 401) {
          this.toastr.error('Incorrect email or password.', 'Authentication Error');
        } else if (error.status === 403) {
          this.toastr.error('Access denied. Please contact support.', 'Authorization Error');
        } else if (error.status === 500) {
          this.toastr.error('Server error. Please try again later.', 'Server Error');
        } else {
          this.toastr.error('An unknown error occurred. Please try again.', 'Error');
        }
      },
    });
  }
}
