export class UserResponse {
    constructor(
      public id: number,
      public name: string,
      public email: string,
      public dateInscription: Date, // Ensure this is typed as Date
      public userRole: string
    ) {}
  }
  