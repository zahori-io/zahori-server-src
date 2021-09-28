import { Component, OnInit } from '@angular/core';
import {Account} from '../../model/account';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PasswordValidator} from '../../validators/passwordValidator';
import {Router} from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  signupForm: FormGroup;
  submitted = false;
  private passwordPattern = '^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$';
  constructor(private formBuilder: FormBuilder, private router: Router) {
    this.createForm();
  }
  createForm(): void{
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
    },  {
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
  }
  goBack(): void{
    this.router.navigate(['']);
  }
}
