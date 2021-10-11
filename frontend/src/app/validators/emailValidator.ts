import { FormGroup } from '@angular/forms';

export function EmailValidator(controlName: string, matchingControlName: string){

  return (formGroup: FormGroup) => {

    const control = formGroup.controls[controlName];

    const matchingControl = formGroup.controls[matchingControlName];

    if (matchingControl.errors && !matchingControl.errors.emailValidator) {

      return;

    }

    if (control.value !== matchingControl.value) {

      matchingControl.setErrors({ emailValidator: true });

    } else {

      matchingControl.setErrors(null);

    }

  }

}
