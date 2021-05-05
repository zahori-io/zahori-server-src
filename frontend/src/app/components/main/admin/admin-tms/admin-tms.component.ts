import { Component, OnInit } from '@angular/core';
import {DataService} from '../../../../services/data.service';
import {ClientTestRepo} from '../../../../model/clientTestRepo';
import {flatMap} from 'rxjs/internal/operators';
import {TestRepository} from '../../../../model/test-repository';
import {BannerOptions} from '../../../../utils/banner/banner';
import Swal from 'sweetalert2';
import {Case} from '../../../../model/case';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-admin-tms',
  templateUrl: './admin-tms.component.html',
  styleUrls: ['./admin-tms.component.css']
})
export class AdminTmsComponent implements OnInit {
  clientTestRepo: ClientTestRepo[];
  testRepo: Map<number, TestRepository>;
  newTest: number[];
  loading = true;
  banner: BannerOptions;
  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    this.banner = new BannerOptions();
    this.dataService.getClientTestRepo().subscribe(
      (clientTestRepo) => {
        this.clientTestRepo = clientTestRepo;
      },
      (error) => {},
      () => {
        this.testRepo = new Map<number, TestRepository>();
        this.newTest = [];
        this.clientTestRepo.forEach(clienttest => {
          this.dataService.getTestRepository(clienttest.id.testRepoId.toString()).subscribe(
            (testRepo) => {
              this.testRepo.set(clienttest.id.testRepoId, testRepo);
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
    clientTestRepo.id.clientId = this.clientTestRepo.length > 0 ?
                                 this.clientTestRepo[this.clientTestRepo.length - 1].id.clientId : 0;
    clientTestRepo.id.testRepoId = this.clientTestRepo.length > 0 ?
                                   this.clientTestRepo[this.clientTestRepo.length - 1].id.testRepoId + 1 : 0;
    this.newTest.push( clientTestRepo.id.testRepoId);
    this.clientTestRepo.push(clientTestRepo);
    this.testRepo.set(clientTestRepo.id.testRepoId, new TestRepository());
  }
  activateSwitch(testRepoId: number, event: any): void {
    event.currentTarget.checked ? this.activate(testRepoId) : this.desactivate(testRepoId);
  }
  save(testRepoId: number, index: number): void{
    const testRepo: TestRepository = this.testRepo.get(testRepoId);
    if (this.newTest.includes(testRepoId)){
      testRepo.testRepoId = 0;
    }
    testRepo.order = 1;
    this.dataService.setTestRepository(testRepo).subscribe(
      (newTestRepo: TestRepository) => {
        this.clientTestRepo[index].id.testRepoId = newTestRepo.testRepoId;
        this.testRepo.delete(testRepoId);
        this.testRepo.set(newTestRepo.testRepoId, newTestRepo);
        this.newTest.splice(this.newTest.indexOf(testRepoId), 1);
        this.dataService.setClientTestRepo(this.clientTestRepo[index]).subscribe(
          () => {},
          (error) => {
            this.banner = new BannerOptions('Error al guardar el repositorio:', error, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Reposiotrio guardado', '', SUCCESS_COLOR, true);
          }
        );
      },
      (error) => {
        this.banner = new BannerOptions('Error al guardar el repositorio:', error, ERROR_COLOR, true);
      },
    );
  }
  private activate(testRepoId: number): void{
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
        this.dataService.setTestRepository(this.testRepo.get(testRepoId)).subscribe(
          (testRepo: TestRepository) => {
            this.testRepo.set(testRepo.testRepoId, testRepo);
          },
          (error) => {
            this.banner = new BannerOptions('Error al activar el repositorio:', error, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Reposiotrio activado', '', SUCCESS_COLOR, true);
          }
        );
      }else{
        this.testRepo.get(testRepoId).active = false;
      }
    });
  }
  private desactivate(testRepoId: number): void{
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
        this.testRepo.get(testRepoId).active = false;
        this.dataService.deleteTestRepository(this.testRepo.get(testRepoId)).subscribe(
          (testRepo: TestRepository) => {
            this.testRepo.set(testRepo.testRepoId, testRepo);
          },
          (error) => {
            this.banner = new BannerOptions('Error al desactivar el repositorio:', error, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Reposiotrio desactivado', '', SUCCESS_COLOR, true);
          }
        );
      }else{
        this.testRepo.get(testRepoId).active = true;
      }
    });
  }

  // tslint:disable-next-line:typedef
  closeBanner(){
    // tslint:disable-next-line:new-parens
    this.banner = new BannerOptions;
  }
}
