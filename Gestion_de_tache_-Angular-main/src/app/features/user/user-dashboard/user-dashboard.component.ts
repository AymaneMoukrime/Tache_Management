import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user/user.service';
import { NavbarComponent } from '../user-navbar/user-navbar.component';
import { CommonModule } from '@angular/common';
import { SidebarUserComponent } from '../sidebar-user/sidebar-user.component';
import { ProjectResponse } from '../../../models/project/project-response.model';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';  // Ensure this is present
import { AuthService } from '../../../services/auth/auth.service';
import { IconSetService } from '@coreui/icons-angular';
import {  cilPencil } from '@coreui/icons';
import { IconModule } from '@coreui/icons-angular';  // Import IconModule
import { ProjectService } from '../../../services/project/project.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css'],
  imports: [CommonModule,FormsModule ,RouterModule ,IconModule], 
  standalone: true, // Import NavbarComponent
})
export class UserDashboardComponent implements OnInit {
[x: string]: any;
cilPencil = cilPencil; 
  projects: ProjectResponse[] = [];
  ownedProjects: ProjectResponse[] = [];
  otherProjects: ProjectResponse[] = [];
  userId: number | null = null; // Declare userId

  constructor(private userService: UserService , private router : Router , private authService: AuthService ,  public iconSet: IconSetService ,  private toastr: ToastrService , private projectService :ProjectService ) {
    iconSet.icons = { cilPencil  };
  }

  ngOnInit(): void {
    this.loadProjects(); // Automatically load projects when component initializes
    
    this.userId = this.authService.getUserIdFromToken();
    console.log('Extracted User ID from Token:', this.userId);
  }

  loadProjects(): void {
    this.userService.getProjectsForUser().subscribe({
      next: (data) => {
        console.log(this.projects);
console.log(this.ownedProjects);
console.log(this.otherProjects);

        console.log('Projects loaded:', data);
        this.projects = data;
        this.ownedProjects = this.projects.filter(project => project.owner?.id === this.userId);
  this.otherProjects = this.projects.filter(project => project.owner?.id !== this.userId);

      },
      error: (err) => {
        console.error('Error fetching projects:', err);
      },
    });
  }

  viewProject(projectId: number): void {
    if (projectId) {
        this.router.navigate(['/user/project', projectId])
      .then(() => console.log(`Navigated to project ${projectId}`))
      .catch(err => console.error('Navigation error:', err));
      } else {
        console.error('Invalid Project ID');
      }
  }
  ProjectPage (projectId: number): void {
    if (projectId) {
        this.router.navigate(['/user/project-dashboard', projectId])
      .then(() => console.log(`Navigated to project ${projectId}`))
      .catch(err => console.error('Navigation error:', err));
      } else {
        console.error('Invalid Project ID');
      }
  }
  deleteProject(projectId: number): void {
    if (confirm('Are you sure you want to delete this project?')) {
      this.projectService.deleteProject(projectId).subscribe({
        next: (response) => {
          console.log('Response from delete:', response); // Debug the response
          if (response.status === 200) {
            this.toastr.success('Project deleted successfully', 'Success');
            this.projects = this.projects.filter((project) => project.id !== projectId);
            this.ownedProjects = this.ownedProjects.filter((project) => project.id !== projectId);
        this.otherProjects = this.otherProjects.filter((project) => project.id !== projectId); // Remove from UI
          }
        },
        error: (err) => {
          console.error('Error while deleting project:', err); // Debug the error
          this.toastr.error('Failed to delete project', 'Error');
        },
      });
    }
  }
  
  
  
  
}
