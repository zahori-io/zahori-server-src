import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../../services/data.service';
import { ClientTestRepo } from '../../../../model/clientTestRepo';
import { TestRepository } from '../../../../model/test-repository';
import { BannerOptions } from '../../../../utils/banner/banner';
import Swal from 'sweetalert2';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-admin-tms',
  templateUrl: './admin-tms.component.html',
  styleUrls: ['./admin-tms.component.css']
})
export class AdminTmsComponent implements OnInit {
  
  clientTestRepos: ClientTestRepo[];
  testRepos: Map<number, TestRepository>;
  newTest: number[];
  loading = true;
  banner: BannerOptions;

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    this.banner = new BannerOptions();
    this.dataService.getClientTestRepos().subscribe(
      (clientTestRepos) => {
        this.clientTestRepos = clientTestRepos;
      },
      (error) => { },
      () => {
        this.testRepos = new Map<number, TestRepository>();
        this.newTest = [];
        this.clientTestRepos.forEach(clienttest => {
          this.dataService.getTestRepository(clienttest.id.testRepoId).subscribe(
            (testRepo) => {
              this.testRepos.set(clienttest.id.testRepoId, testRepo);
            },
            (error) => {
              this.banner = new BannerOptions('Error al cargar los repositorios:', error, ERROR_COLOR, true);
            }
          );
        });
        this.loading = false;
      }
    );
  }

  newTestRepository(): void {
    const clientTestRepo = new ClientTestRepo();
    clientTestRepo.id.clientId = this.clientTestRepos.length > 0 ?
      this.clientTestRepos[this.clientTestRepos.length - 1].id.clientId : 0;
    clientTestRepo.id.testRepoId = this.clientTestRepos.length > 0 ?
      this.clientTestRepos[this.clientTestRepos.length - 1].id.testRepoId + 1 : 0;
    this.newTest.push(clientTestRepo.id.testRepoId);
    this.clientTestRepos.push(clientTestRepo);
    this.testRepos.set(clientTestRepo.id.testRepoId, new TestRepository());
  }

  activateSwitch(clientTestRepo: ClientTestRepo, event: any): void {
    event.currentTarget.checked ? this.activate(clientTestRepo) : this.desactivate(clientTestRepo);
  }

  save(testRepoId: number, index: number): void {
    const testRepo: TestRepository = this.testRepos.get(testRepoId);
    if (this.newTest.includes(testRepoId)) {
      testRepo.testRepoId = 0;
    }
    
    testRepo.order = 1;
    this.dataService.saveTestRepository(testRepo).subscribe(
      (newTestRepo: TestRepository) => {
        this.clientTestRepos[index].id.testRepoId = newTestRepo.testRepoId;
        this.testRepos.delete(testRepoId);
        this.testRepos.set(newTestRepo.testRepoId, newTestRepo);
        this.newTest.splice(this.newTest.indexOf(testRepoId), 1);
        this.dataService.saveClientTestRepo(this.clientTestRepos[index]).subscribe(
          () => { },
          (error) => {
            this.banner = new BannerOptions('Error al guardar el repositorio:', error.message, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Reposiotrio guardado', '', SUCCESS_COLOR, true);
          }
        );
      },
      (error) => {
        this.banner = new BannerOptions('Error al guardar el repositorio:', error.message, ERROR_COLOR, true);
      },
    );
  }

  private activate(clientTestRepo: ClientTestRepo): void {
    Swal.fire({
      title: 'Activar Repositorio',
      text: 'Esta acción reactivara el repositorio quedando disponible',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Activar',
      cancelButtonText: 'Cancelar',
      backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
    }).then((result) => {
      if (result.value) {
        clientTestRepo.active = true;
        this.dataService.saveClientTestRepo(clientTestRepo).subscribe(
          (clientTestRepoReturned: ClientTestRepo) => {
            clientTestRepo = clientTestRepoReturned;
          },
          (error) => {
            this.banner = new BannerOptions('Error al activar el repositorio:', error.message, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Reposiotrio activado', '', SUCCESS_COLOR, true);
          }
        );
      } else {
        clientTestRepo.active = false;
      }
    });
  }
  
  private desactivate(clientTestRepo: ClientTestRepo): void {
    Swal.fire({
      title: 'Desactivar Repositorio',
      text: 'Esta acción desactivara el repositorio',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Desactivar',
      cancelButtonText: 'Cancelar',
      backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
    }).then((result) => {
      if (result.value) {
        clientTestRepo.active = false;
        this.dataService.saveClientTestRepo(clientTestRepo).subscribe(
          (clientTestRepoReturned: ClientTestRepo) => {
            clientTestRepo = clientTestRepoReturned;
          },
          (error) => {
            this.banner = new BannerOptions('Error al desactivar el repositorio:', error.message, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Reposiotrio desactivado', '', SUCCESS_COLOR, true);
          }
        );
      } else {
        clientTestRepo.active = true;
      }
    });
  }

  // tslint:disable-next-line:typedef
  closeBanner() {
    // tslint:disable-next-line:new-parens
    this.banner = new BannerOptions;
  }
}
