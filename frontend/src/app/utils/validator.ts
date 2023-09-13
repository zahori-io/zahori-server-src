export class Validator {

  public static invalidEmail: string = "validator.email";
  public static invalidPassword: string = "validator.password";

  constructor() { }

  public static isValidEmail(email: string): boolean {
    return /^[a-zA-Z0-9]+([\._-]?[a-zA-Z0-9]+)*@[a-zA-Z0-9]+([\.-]?[a-zA-Z0-9]+)*(\.[a-zA-Z0-9]{2,})+$/.test(email);
  }

  public static isValidPassword(password: string): boolean {
    return /^(?=.*[0-9].{0,})(?=.*[a-z].{0,})(?=.*[A-Z].{0,})(?=.*[*.!¡'`"ªº@çÇ#$%^&(){}[\]:;<>,.¿?/~_+\-=|\\].{0,}).{8,}$/.test(password);
  }
}
