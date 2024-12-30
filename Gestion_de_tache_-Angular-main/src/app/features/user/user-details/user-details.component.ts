import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth/auth.service';
import { UserService } from '../../../services/user/user.service';
import { ToastrService } from 'ngx-toastr';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';  // Ensure this is present
import { CommonModule } from '@angular/common';
import { IconModule } from '@coreui/icons-angular';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.css'],
  imports: [CommonModule,FormsModule ,RouterModule ,IconModule]
})
export class UserDetailsComponent implements OnInit {
  user = {
    name: '',
    email: '',
    image: null,
  };
  selectedImage: File | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchUserDetails();
  }

  // Fetch current user details
  fetchUserDetails(): void {
    this.userService.getUserDetails().subscribe({
      next: (response) => {
        this.user.name = response.name;
        this.user.email = response.email;
      },
      error: (err) => {
        this.toastr.error('Failed to fetch user details', 'Error');
      },
    });
  }

  // Update user info (name/email)
  updateUserInfo(): void {
    this.userService.updateUserInfo(this.user).subscribe({
      next: () => {
        this.toastr.success('User info updated successfully', 'Success');
      },
      error: () => {
        this.toastr.error('Failed to update user info', 'Error');
      },
    });
  }

 
}
