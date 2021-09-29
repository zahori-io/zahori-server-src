import { Component, OnInit } from '@angular/core';
import {Account} from '../../model/account';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PasswordValidator} from '../../validators/passwordValidator';
import {Router} from '@angular/router';
import {DataService} from '../../services/data.service';
import {BannerOptions} from '../utils/banner/banner';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  banner: BannerOptions;
  signupForm: FormGroup;
  submitted = false;
  private passwordPattern = '^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$';
  constructor(private formBuilder: FormBuilder, private router: Router, public dataService: DataService) {
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
    this.setSignUp(account);
    this.goBack();
  }
  goBack(): void{
    this.router.navigate(['']);
  }
  setSignUp(account: Account): void {
    this.dataService.setSignUpUser(account).subscribe(result => {
      this.banner = new BannerOptions('Usuario creado', 'Usuario creado correctamente', SUCCESS_COLOR, true);
    }, error => {
      this.banner = new BannerOptions('Error', 'Error: ' + error.message, ERROR_COLOR, true);
      console.log('Error: ' + error.message);
    });
  }
}
