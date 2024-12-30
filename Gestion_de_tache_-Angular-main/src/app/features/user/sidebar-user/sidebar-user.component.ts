import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { ProjectResponse } from '../../../models/project/project-response.model';
import { RouterModule } from '@angular/router';


@Component({
  standalone: true,
  selector: 'app-sidebar-user',
  imports: [RouterModule],
  templateUrl: './sidebar-user.component.html',
  styleUrls: ['./sidebar-user.component.css']
})
export class SidebarUserComponent implements OnInit {
  projects: ProjectResponse[] = [];

  constructor(private userService: UserService, private router: Router) {}
  
  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.userService.getProjectsForUser().subscribe({
      next: (data: ProjectResponse[]) => {
        this.projects = data;
        console.log('Projects loaded:', this.projects);
      },
      error: (err) => {
        console.error('Error fetching projects:', err);
      },
    });
  }
  logout(): void {
    // Remove token from local storage or session storage
    localStorage.removeItem('token');
    // Navigate to login page
    this.router.navigate(['/login']);
  }

  
}
