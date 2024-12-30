import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../../services/project/project.service';
import { ProjectResponse } from '../../../models/project/project-response.model';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { TeamService } from '../../../services/team/team.service';
import { TeamDto } from '../../../models/team/teamDto.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  imports: [RouterModule, FormsModule, CommonModule],
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css'],
})
export class ProjectComponent implements OnInit {
  projectId: number = 0;
  projectOwnerId: number | null = null;
  projectOwnerEmail: string | null = null;

  project: ProjectResponse = {
    id: 0,
    name: '',
    description: '',
    startDate: new Date(),
    endDate: new Date(),
    owner: null,
  };
  newTeamName: string = '';
  newUserEmail: string = '';
  isLoading: boolean = true;
  errorMessage: string = '';
  projectUsers: string[] = [];
  projectTeams: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private teamService: TeamService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/sign-in']);
    } else {
      const projectIdFromRoute = this.route.snapshot.paramMap.get('projectId');
      this.projectId = projectIdFromRoute ? +projectIdFromRoute : 0;

      if (this.projectId > 0) {
        this.initializeProject();
      }
    }
  }

  // Initialize project details and related data
  initializeProject(): void {
    this.isLoading = true;
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.projectOwnerId = project.owner?.id || null;
        this.projectOwnerEmail = project.owner?.email || null;
        this.project.startDate = project.startDate || new Date();
        this.project.endDate = project.endDate || new Date();

        this.loadProjectUsers();
  
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching project:', err);
        this.toastr.error('Failed to load project details.', 'Error');
        this.isLoading = false;
      },
    });
  }

  onUpdateProject(): void {
    if (this.project.endDate && this.project.startDate && this.project.endDate < this.project.startDate) {
      this.toastr.error('End date cannot be before the start date.', 'Error');
      return;
    }

    this.projectService.updateProject(this.projectId, this.project).subscribe({
      next: () => {
        this.toastr.success('Project updated successfully!', 'Success');
      },
      error: (err) => {
        console.error('Error updating project:', err);
        this.toastr.error('Failed to update project. Please check your input.', 'Error');
      },
    });
  }

  loadProjectUsers(): void {
    this.projectService.getProjectUsers(this.projectId).subscribe({
      next: (users) => {
        this.projectUsers = (users || []).filter((userEmail) => userEmail !== this.projectOwnerEmail);
      },
      error: (err) => {
        console.error('Error loading project users:', err);
        this.toastr.error('Error loading project users.', 'Error');
      },
    });
  }

  

  addUserToProject(): void {
    if (!this.newUserEmail.trim()) {
      this.toastr.warning('Please enter a valid email', 'Warning');
      return;
    }

    this.projectService.addUserToProjectByEmail(this.projectId, this.newUserEmail).subscribe({
      next: () => {
        this.loadProjectUsers();
        this.clearFormFields();
        this.toastr.success('User added to project successfully!', 'Success');
      },
      error: (err) => {
        console.error('Error adding user to project:', err);
        this.toastr.error('No user with this email .  Please check your input ', 'Error');
      },
    });
  }

  removeUserFromProject(userEmail: string): void {
    this.projectService.removeUserFromProjectByEmail(this.projectId, userEmail).subscribe({
      next: () => {
        this.loadProjectUsers();
        this.toastr.success('User removed from project successfully!', 'Success');
      },
      error: (err) => {
        console.error('Error removing user from project:', err);
        this.toastr.error('Error removing user from project.', 'Error');
      },
    });
  }



  clearFormFields(): void {
    this.newUserEmail = '';
    this.errorMessage = '';
  }
}
