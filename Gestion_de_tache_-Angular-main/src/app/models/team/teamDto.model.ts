// team-dto.model.ts
import { UserResponse } from "../user/user-response.model";  // Adjust the import path based on your file structure

export interface TeamDto {
  id: number | null;
  name: string;
  dateCreation: Date;
  projectId: number;
  projectName: string;
  users: UserResponse[]; // Replace `any[]` with the correct type
}