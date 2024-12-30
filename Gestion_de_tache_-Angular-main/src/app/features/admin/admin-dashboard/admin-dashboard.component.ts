import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SidebarAdminComponent } from '../sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-admin-dashboard',
  imports: [RouterModule,SidebarAdminComponent],
  standalone: true, // Mark as standalone
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {

}
