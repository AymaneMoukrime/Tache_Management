import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserRoutingModule } from './user-routing.module';  // Import UserRoutingModule
import { RouterModule } from '@angular/router';
import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import { SidebarUserComponent } from './sidebar-user/sidebar-user.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectComponent } from './project/project.component';
import { TeamDashboardComponent } from './team-dashboard/team-dashboard.component';
import { TeamManagementComponent } from './team-management/team-management.component';
import { IconModule, IconSetService } from '@coreui/icons-angular';
import { UserDetailsComponent } from './user-details/user-details.component';

@NgModule({
 
  imports: [
   
    RouterModule,
    IconModule,
    CommonModule,  // Ensure CommonModule is included
    UserRoutingModule ,
    TeamDashboardComponent,
    TeamManagementComponent,
    DashboardComponent,
    UserDetailsComponent,
    ProjectComponent,
    SidebarUserComponent,
    UserDashboardComponent, 
    CreateProjectComponent,
    UserRoutingModule// Include UserRoutingModule here
  ],
  providers :[IconSetService]
})
export class UserModule {}
