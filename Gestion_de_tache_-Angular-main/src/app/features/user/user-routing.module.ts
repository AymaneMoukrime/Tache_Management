import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectComponent } from './project/project.component'
import { projectDashboardComponent } from './project-dashboard/project-dashboard.component';
import { TeamDashboardComponent } from './team-dashboard/team-dashboard.component';
import { TeamManagementComponent } from './team-management/team-management.component';
import { TaskDetailsComponent } from './task-details/task-details.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { SignInComponent } from '../auth/sign-in/sign-in.component';


const routes: Routes = [
  {
    path: '',
    component:  DashboardComponent  , // Sidebar as the parent container
    children: [
      { path: '', redirectTo: 'userdashboard', pathMatch: 'full' },
      {path : 'userdashboard' , component : UserDashboardComponent},
      { path: 'dashboard', component: DashboardComponent },
      { path: 'create-project', component: CreateProjectComponent },
      {path :'project/:projectId' , component : ProjectComponent },
      {path :'project-dashboard/:projectId' , component : projectDashboardComponent},
      {path :'team-dashboard' , component : TeamDashboardComponent},
      { path: 'team-management/:projectid/:teamId', component: TeamManagementComponent },
      { path: 'task-detail/:projectId/:taskId', component: TaskDetailsComponent },
      { path: 'user-details', component: UserDetailsComponent },
      



      
    
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserRoutingModule {}
