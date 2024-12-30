// task-dto.model.ts

export interface TaskDto {
    id: number;                     // Task ID
    title: string;                  // Title of the task
    description: string;            // Description of the task
    dateCreation: Date;             // Creation date of the task
    dateDeadline: Date | null;             // Deadline date of the task
    priorite: number;               // Priority of the task
    status: TaskStatus;             // Status of the task (e.g., "en cours", "terminer")
    couleur: string;                // Color associated with the task
    taskOwnerEmail: string | null;         // Email of the task owner
    taskUserEmail: string | null;          // Email of the assigned user
  }
  
  // Enum for TaskStatus
  export type TaskStatus = 'DONE' | 'TODO' | 'IN_PROGRESS';

  