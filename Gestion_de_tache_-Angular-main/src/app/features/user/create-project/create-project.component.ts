import { Component } from '@angular/core';
import { ProjectService } from '../../../services/project/project.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Add this
import { ToastrService } from 'ngx-toastr'; // Import ToastrService

@Component({
  standalone: true,
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  imports: [CommonModule, FormsModule], 
  styleUrls: ['./create-project.component.css'],
})
export class CreateProjectComponent {
  project = {
    name: '',
    description: '',
  };
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private projectService: ProjectService , private toastr: ToastrService) {}

  onSubmit(): void {
    console.log('Submitting project:', this.project);
    this.projectService.createProject(this.project).subscribe({
      next: (response) => {
        this.toastr.success('Project created successfully!', 'Success');
        
      },
      error: (error) => {
        if (error.status === 500) {
          this.toastr.error('Server error. Please try again later.', 'Server Error');
        }
        console.error('Error creating project:', error);
        this.toastr.warning('Failed to create project.  Please check your input.', 'Error');
      }
    });
  }

 
}
