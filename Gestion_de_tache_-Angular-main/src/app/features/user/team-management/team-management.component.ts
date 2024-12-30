import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team/team.service'; // Adjust the path as needed
import { UserService } from '../../../services/user/user.service'; // Adjust the path as needed
import { TeamDto } from '../../../models/team/teamDto.model'; // Adjust the path as needed
import { UserResponse } from '../../../models/user/user-response.model'; // Adjust the path as needed
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr'; // Import ToastrService

@Component({
  selector: 'app-team-management',
  templateUrl: './team-management.component.html',
  styleUrls: ['./team-management.component.css'],
  imports: [CommonModule, FormsModule],
})
export class TeamManagementComponent implements OnInit {
  teamId: number = 0;
  teamName: string = '';
  projectId: number = 0;
  team: TeamDto | null = null;
  users: UserResponse[] = [];
  selectedUserEmail: string = '';
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private teamService: TeamService,
    private userService: UserService,
    private router: Router,
    private toastr: ToastrService // Inject ToastrService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const projectId = params.get('projectid'); // Get projectId from URL
      const teamId = params.get('teamId'); // Get teamId from URL

      if (projectId && teamId) {
        this.projectId = +projectId; // Convert to number if not null
        this.teamId = +teamId; // Convert to number if not null
        this.loadTeamDetails(); // Fetch team details by teamId and projectId
      } else {
        this.toastr.error('Missing projectId or teamId.', 'Error');
      }
    });
  }

  loadTeamDetails(): void {
    if (this.projectId && this.teamId) {
      this.teamService.getTeamById(this.projectId, this.teamId).subscribe({
        next: (team: TeamDto) => {
          this.team = team;
          this.users = team.users || []; // Populate users list
          this.toastr.success('Team details loaded successfully.', 'Success');
        },
        error: (err) => {
          console.error('Error fetching team details:', err);
          this.toastr.error('Error loading team details.', 'Error');
        },
      });
    } else {
      this.toastr.error('Missing projectId or teamId.', 'Error');
    }
  }

  addUserToTeam(): void {
    if (this.selectedUserEmail.trim() === '') {
      this.toastr.warning('Email cannot be empty.', 'Warning');
      return;
    }

    this.teamService.addUserToTeam(this.projectId || 0, this.teamId, this.selectedUserEmail).subscribe({
      next: (updatedTeam) => {
        this.team = updatedTeam;
        this.users = updatedTeam.users || [];
        this.selectedUserEmail = ''; // Reset the input field
        this.toastr.success('User added to team successfully.', 'Success');
      },
      error: (err) => {
        if (err.status === 403) {
          this.toastr.warning('Invalide email . Please check your imputs');
        }
        console.error('Error adding user:', err);
        this.toastr.error('Error adding user to team.', 'Error');
      },
    });
  }

  removeUserFromTeam(userEmail: string): void {
    if (!userEmail.trim()) {
      this.toastr.warning('Email cannot be empty.', 'Warning');
      return;
    }

    this.teamService.removeUserFromTeam(this.projectId || 0, this.teamId, userEmail).subscribe({
      next: (updatedTeam) => {
        this.team = updatedTeam;
        this.users = updatedTeam.users || [];

        this.toastr.success('User removed from team successfully.', 'Success');

        // Check if the team has no more users
        if (this.users.length === 0) {
          this.router.navigate(['/user/team-dashboard']).then(() => {
            this.toastr.info('The team is empty and has been removed.', 'Info');
          });
        }
      },
      error: (err) => {
        console.error('Error removing user from team:', err);
        this.toastr.error('Error removing user from team.', 'Error');
      },
    });
  }
}
