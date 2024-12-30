import { Component, OnInit } from '@angular/core';
import { ProjectService } from '../../../services/project/project.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team/team.service'; // Import your team service
import { TeamDto } from '../../../models/team/teamDto.model'; // Import your Team DTO
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Add this import
import { ToastrService } from 'ngx-toastr'; // Import ToastrService

@Component({
  imports: [
    CommonModule,
    FormsModule, // Add this to the imports array
  ],
  selector: 'app-team-dashboard',
  templateUrl: './team-dashboard.component.html',
  styleUrls: ['./team-dashboard.component.css'],
})
export class TeamDashboardComponent implements OnInit {
  selectedProjectId: number = 0;
  projects: any[] = [];
  projectId: number = 0;
  teams: any[] = [];
  teamName: string = ''; // Bind for team name input field
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService, // Corrected
    private teamService: TeamService, // Inject the TeamService
    private router: Router,
    private toastr: ToastrService // Inject ToastrService
  ) {}

  ngOnInit(): void {
    this.loadProjects();
    this.loadTeams();
  }

  loadProjects(): void {
    this.projectService.getProjectsForUser().subscribe({
      next: (projects: any[]) => {
        this.projects = projects;
        this.toastr.success('Projects loaded successfully.', 'Success');
      },
      error: (err) => {
        console.error('Error fetching projects:', err);
        this.toastr.error('Error loading projects.', 'Error');
      },
    });
  }

  loadTeams(): void {
    this.teamService.getUserTeams().subscribe({
      next: (teams: TeamDto[]) => {
        this.teams = teams;

        // Fetch project names for each team
        this.teams.forEach((team) => {
          if (team.projectid) {
            this.projectService.getProjectById(team.projectid).subscribe({
              next: (project) => {
                team.projectName = project.name;
              },
              error: (err) => {
                console.error('Error fetching project:', err);
                this.toastr.error('Error loading project details for a team.', 'Error');
              },
            });
          }
        });

        this.toastr.success('Teams loaded successfully.', 'Success');
      },
      error: (err) => {
        console.error('Error loading teams:', err);
        this.toastr.error('Error loading teams.', 'Error');
      },
    });
  }

  createTeam(): void {
    if (this.teamName.trim() === '') {
      this.toastr.warning('Team name cannot be empty.', 'Warning');
      return;
    }

    if (this.selectedProjectId === 0) {
      this.toastr.warning('Please select a project.', 'Warning');
      return;
    }

    const newTeam: TeamDto = {
      id: null,
      name: this.teamName.trim(),
      dateCreation: new Date(),
      projectName: '',
      projectId: this.selectedProjectId,
      users: [],
    };

    this.teamService.createTeam(this.selectedProjectId, newTeam).subscribe({
      next: (team) => {
        this.teams.push({
          ...team,
          projectId: this.selectedProjectId,
        });
        this.teamName = ''; // Clear the input field after success
        this.toastr.success('Team created successfully!', 'Success');
        this.loadTeams();
      },
      error: (err) => {
        console.error('Error creating team:', err);
        this.toastr.error('Failed to create team.', 'Error');
      },
    });
  }

  viewTeam(projectid: number, teamId: number): void {
    if (projectid && teamId) {
      this.router.navigate([`user/team-management/${projectid}/${teamId}`])
        .then(() => this.toastr.info(`Navigated to team management for Team ID=: ${teamId}`, 'Info'))
        .catch((err) => {
          console.error('Navigation error:', err);
          this.toastr.error('Failed to navigate to team management.', 'Error');
        });
    } else {
      this.toastr.error('Missing projectId or teamId.', 'Error');
    }
  }

  deleteTeam(projectId: number, teamId: number): void {
    this.teamService.deleteTeam(projectId, teamId).subscribe({
      next: () => {
        this.teams = this.teams.filter((team) => team.id !== teamId);
        this.toastr.success('Team deleted successfully!', 'Success');
      },
      error: (err) => {
        console.error('Error deleting team:', err);
        this.toastr.error('Error deleting team.', 'Error');
      },
    });
  }
}
