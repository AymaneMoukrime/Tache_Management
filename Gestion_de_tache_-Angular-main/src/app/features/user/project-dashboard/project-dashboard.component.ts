import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TaskService } from '../../../services/task/task.service';  // Adjust path if needed
import { TaskDto } from '../../../models/task/taskDto.model';  // Adjust path if needed
import { DashboardComponent } from '../dashboard/dashboard.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../../services/project/project.service';
import { ProjectResponse } from '../../../models/project/project-response.model';
import { AuthService } from '../../../services/auth/auth.service';
import { ToastrService } from 'ngx-toastr'; // Import ToastrService

@Component({
  imports: [CommonModule, FormsModule, RouterModule],
  selector: 'app-task-management',
  templateUrl: './project-dashboard.component.html',
  styleUrls: ['./project-dashboard.component.css'],

})



export class projectDashboardComponent implements OnInit {
  projectId: number = 0;
  tasks: TaskDto[] = [];
  filteredTasks: TaskDto[] = [];
  currentUserId: number | null = null;
  projectOwnerId: number | null = null;
  // Declare projectOwnerId
  newTask: TaskDto = {
    id: 0,
    title: '',
    description: '',
    status: 'TODO',
    dateDeadline: null,
    dateCreation: new Date(), // Add default value
    priorite: 1, // Default priority
    couleur: '#000000',
    taskOwnerEmail : '', taskUserEmail : ''
    
  };


  constructor(private route: ActivatedRoute, private taskService: TaskService, private projectService: ProjectService, private authService: AuthService , private toastr: ToastrService ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('projectId'); // Get projectId from URL
      if (id) {
        this.projectId = +id; // Convert to number
        this.currentUserId = this.authService.getUserIdFromToken();
        this.loadTasks();

        // Fetch project owner and log only after completion
        this.fetchProjectOwner();
      }

      // Log the Project ID and Current User ID
      console.log('Project ID:', this.projectId);
      console.log('Current User ID:', this.currentUserId);
    });
  }

  // Fetch all tasks for the project
  loadTasks(): void {
    this.taskService.getTask(this.projectId).subscribe(
      (tasks) => {
        this.tasks = tasks; // Store all tasks
        console.log(tasks);
        this.filteredTasks = tasks;
        
      },
      (err) => {
        console.error('Error fetching tasks:', err);
        this.toastr.error('Failed to load tasks.', 'Error');
      }
    );
  }

  // Filter tasks based on the selected filter (ongoing, completed, delayed)
  filterTasks(filter: string): void {
    if (filter === 'DONE') {
      this.filteredTasks = this.tasks.filter((task) => task.status === 'DONE');
    } else if (filter === 'IN_PROGRESS') {
      this.filteredTasks = this.tasks.filter((task) => task.status === 'IN_PROGRESS');
    }
    else if (filter === 'TODO') {
      this.filteredTasks = this.tasks.filter((task) => task.status === 'TODO');
    }
    else if (filter === 'ALL Tasks') {
      this.filteredTasks = this.tasks;
    }
    else if (filter === 'delayed') {
      this.filteredTasks = this.tasks.filter(
        (task) =>
          task.status != 'DONE' &&
          task.dateDeadline !== null && // Ensure date_Deadline is not null
          new Date(task.dateDeadline) < new Date()
      );
    }
    else if (filter === 'MyTask') {
      const currentUserEmail = this.authService.getUserEmailFromToken(); // Get the current user's email
      this.filteredTasks = this.tasks.filter(
        (task) => task.taskUserEmail === currentUserEmail
      );
    }
  }

  updateTaskStatus(task: TaskDto): void {
    // Toggle between statuses: IN_PROGRESS -> DONE -> TODO
    let newStatus = task.status;
    if (task.status === 'IN_PROGRESS') {
      newStatus = 'DONE';
    } else if (task.status === 'DONE') {
      newStatus = 'TODO';
    } else if (task.status === 'TODO') {
      newStatus = 'IN_PROGRESS';
    }

    // Call the service to update the status
    this.taskService.setTaskStatus(task.id, newStatus).subscribe({
      next: () => {
        task.status = newStatus; // Update the status locally after success
        this.toastr.success(
          `Task ${task.id} status updated to ${newStatus}.`,
          'Success'
        );
      },
      error: (err) => {
        console.error('Error updating task status:', err);
        this.toastr.error('Failed to update task status.', 'Error');
      },
    });
  }



  fetchProjectOwner(): void {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (project: ProjectResponse) => {
        console.log('Project Response:', project); // Debugging the response
        this.projectOwnerId = project.owner?.id || null;
        if (this.projectOwnerId !== null) {
          console.log('Owner ID successfully retrieved:', this.projectOwnerId);

          // Now that you have the projectOwnerId, log it or perform actions here
          console.log('Project Owner ID:', this.projectOwnerId);
        } else {
          console.log('Owner ID is null, check backend response or mapping.');
        }
      },
      error: (err: any) => {
        console.error('Error fetching project owner:', err);
      },
    });
  }
  createTask(): void {
    if (!this.newTask.title || !this.newTask.description || !this.newTask.priorite || !this.newTask.couleur) {
      this.toastr.warning('All fields are required to create a task.', 'Warning');
      return;
    }
  
    const taskBody = {
      titre: this.newTask.title,
      description: this.newTask.description,
      priorite: this.newTask.priorite,
      couleur: this.newTask.couleur,
    };
  
    this.taskService.createTask(this.projectId, taskBody).subscribe({
      next: (createdTask) => {
        console.log('Task created successfully:', createdTask);
        this.tasks.push(createdTask);
        this.filteredTasks = this.tasks;
        this.toastr.success('Task created successfully.', 'Success');
      },
      error: (err) => {
        console.error('Error creating task:', err);
        this.toastr.error('Failed to create task.', 'Error');
      },
    });
    
    
  }
  
  deleteTask(taskId: number): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(this.projectId, taskId).subscribe({
        next: () => {
           // Reload the task list
          // Remove the deleted task from the local task list
          this.tasks = this.tasks.filter((task) => task.id !== taskId);
          this.filteredTasks = this.tasks;
         
        },
        error: (err) => {
          if (err.status === 200) {
            this.loadTasks();
            this.toastr.success('Task deleted successfully.', 'Success');
          }
          else{
            console.error('Error deleting task:', err);
            this.toastr.error('Failed to delete task.', 'Error');
          }
          
        },
      });
    }
  }
  


}
