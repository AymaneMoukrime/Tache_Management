import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../services/admin/admin.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {UserResponse} from '../../../models/user/user-response.model'


@Component({
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css'],
})
export class UserManagementComponent implements OnInit {
  users: UserResponse[] = []; // List to hold all user data
  searchQuery: string = ''; // Search input value
  filteredUsers: UserResponse[] = []; // Filtered list for search results
  roles: string[] = ['ADMIN', 'NORMAL'];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  // Fetch all users from backend
  loadUsers(): void {
    console.log('Fetching all users from API...');
    this.adminService.getAllUsers().subscribe({
      next: (data) => {
        console.log('Users fetched:', data);
        this.users = data;
        this.filteredUsers = data; // Initially display all users
      },
      error: (error) => {
        console.error('Error loading users:', error);
      },
    });
  }

  // Search users by name or email
  searchUsers(): void {
    if (this.searchQuery.trim()) {
      this.filteredUsers = this.users.filter(
        (user) =>
          user.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          user.email.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    } else {
      this.filteredUsers = [...this.users]; // Reset to all users when search is cleared
    }
  }

  deleteUser(email: string): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.adminService.deleteUserByEmail(email).subscribe({
        next: (response) => {
          console.log('Delete response:', response);
          alert('User deleted successfully.');
          this.loadUsers(); // Reload the user list
        },
        error: (err) => {
          console.error('Error deleting user:', err.message);
          alert('Failed to delete user.');
        },
      });
    }
  }
  updateUserRole(email: string, event: Event): void {
    const target = event.target as HTMLSelectElement;
    const newRole = target.value;
  
    if (newRole) {
      this.adminService.updateUserRole(email, newRole).subscribe({
        next: (response) => {
          console.log('Role updated successfully:', response);
          alert('Role updated successfully!');
          this.loadUsers(); // Recharge la liste des utilisateurs
        },
        error: (err) => {
          console.error('Error updating role:', err);
          alert('Failed to update role. Please try again.');
        },
      });
    }
  }
  

  
}
