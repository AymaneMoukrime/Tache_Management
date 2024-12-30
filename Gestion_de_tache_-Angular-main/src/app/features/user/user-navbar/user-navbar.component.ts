import { Component, Input } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-user-navbar',
  templateUrl: './user-navbar.component.html',
  styleUrls: ['./user-navbar.component.css'],
})
export class NavbarComponent {
  @Input() userName!: string;
  @Input() userImage!: string;
  @Input() path!: string; // Declare path as an @Input property
}
