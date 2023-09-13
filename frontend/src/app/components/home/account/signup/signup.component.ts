import { Component, OnInit } from '@angular/core';
import { Account } from '../../../../model/account';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordValidator } from '../../../../validators/passwordValidator';
import { Router } from '@angular/router';
import { DataService } from '../../../../services/data.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  signupForm: FormGroup;
  submitted = false;
  errorMessage: string;

  private passwordPattern = /^(?=.*[0-9].{0,})(?=.*[a-z].{0,})(?=.*[A-Z].{0,})(?=.*[*.!¡'`"ªº@çÇ#$%^&(){}[\]:;<>,.¿?/~_+\-=|\\].{0,}).{8,}$/;

  constructor(private formBuilder: FormBuilder, private router: Router, public dataService: DataService) {
    this.createForm();
  }

  createForm(): void {
    this.signupForm = this.formBuilder.group({
      username: ['',
        [
          Validators.required,
          Validators.minLength(4)
        ]
      ],
      password: ['',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(this.passwordPattern)
        ]
      ],
      repPassword: ['',
        [
          Validators.required,
          Validators.minLength(8)
        ]
      ]
    }, {
      validators: [PasswordValidator('password', 'repPassword')]
    });
  }
  
  ngOnInit(): void {
  }

  signup(): void {
    this.submitted = true;
    if (this.signupForm.invalid) {
      return;
    }
    const account = new Account(this.signupForm.get('username').value, this.signupForm.get('password').value);
    this.setSignUp(account);
    this.goBack();
  }
  
  goBack(): void {
    this.router.navigate(['']);
  }

  setSignUp(account: Account): void {
    this.errorMessage = null;
    
    this.dataService.setSignUpUser(account).subscribe(result => {
      // TODO
      this.router.navigateByUrl('/login', { state: { message: 'Revisa tu bandeja de entrada y confirma tu correo para terminar de crear tu cuenta', error: '' } });
    }, error => {
      this.errorMessage = error.message;
      console.log('Error: ' + error.message);
    });
  }

}
