import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { SidebarAdminComponent } from './sidebar-admin/sidebar-admin.component';
import { UserManagementComponent } from './user-management/user-management.component';

@NgModule({
  imports: [
    CommonModule,
    AdminRoutingModule,
    AdminDashboardComponent,
    SidebarAdminComponent, // Import standalone component
    UserManagementComponent,
  ],
})
export class AdminModule {}
