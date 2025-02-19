export class Validator {

  private static emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  static isValidEmail(email: string): boolean {
    return this.emailRegex.test(email);
  }

  static isValidUsername(username: string): boolean {
    for (let i = 0; i < username.length; i++) {
      if (!username[i].match(/[a-zA-Z0-9]/)) {
        return false;
      }
    }
    return true;
  }

  static isValidPassword(password: string): boolean {
    return password.length >= 4;
  }

}
