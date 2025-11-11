export class User {
  isAuthenticated: boolean;
  name: string;
  constructor(isAuthenticated: boolean, name: string) {
    this.isAuthenticated = isAuthenticated;
    this.name = name;
  }
}