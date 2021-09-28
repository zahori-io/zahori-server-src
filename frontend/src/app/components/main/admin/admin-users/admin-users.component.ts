import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PasswordValidator} from '../../../../validators/passwordValidator';
import {EmailValidator} from '../../../../validators/emailValidator';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
  userForm: FormGroup;
  submit = false;
  private passwordPattern = '^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$';
  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.createUserForm();
  }
  createUserForm(): void {
    this.userForm = this.formBuilder.group({
      username: ['',
        [
          Validators.required,
          Validators.minLength(4)
        ]
      ],
      name: ['',
        [
          Validators.required,
          Validators.minLength(4)
        ]
      ],
      surnames: ['',
        [
          Validators.required,
          Validators.minLength(4)
        ]
      ],
      email: ['',
        [
          Validators.required,
          Validators.email
        ]
      ],
      repEmail: ['',
        [
          Validators.required,
          Validators.email
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
      validators: [PasswordValidator('password', 'repPassword'), EmailValidator('email', 'repEmail')]
    });
  }
  onSubmit(): void{
    this.submit = true;
    if (this.userForm.invalid) {
      return;
    }
    console.log('enviado');
  }
}
