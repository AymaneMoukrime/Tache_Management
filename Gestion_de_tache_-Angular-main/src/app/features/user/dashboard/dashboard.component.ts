import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SidebarUserComponent } from '../sidebar-user/sidebar-user.component';
import { NavbarComponent } from '../user-navbar/user-navbar.component';
import { IconModule, IconSetService } from '@coreui/icons-angular';
import { cilMenu } from '@coreui/icons';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  
  imports: [RouterModule,SidebarUserComponent,NavbarComponent , IconModule ,CommonModule ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  sidebarVisible: boolean = true;  // Default value for the sidebar visibility
  cilMenu = cilMenu;

  constructor(private iconSet: IconSetService) {
    iconSet.icons = { cilMenu };  // Set the icon for the toggle button
  }

  // Method to toggle the sidebar visibility
  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }
}


