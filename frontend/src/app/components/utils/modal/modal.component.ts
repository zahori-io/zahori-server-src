import { Component, ComponentFactoryResolver, ComponentRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, Type, ViewChild, ViewContainerRef } from "@angular/core";

@Component({
    selector: 'modal',
    templateUrl: './modal.component.html',
    styleUrls: ['./modal.component.css']
})
export class ModalComponent implements OnInit, OnDestroy, OnChanges {

    @ViewChild('childComponent', { static: true, read: ViewContainerRef }) private childComponent!: ViewContainerRef;

    @Input() title: string;
    @Input() componentType: any;
    @Input() display: boolean;
    @Input() data: any;
    @Input() closable: boolean = true;

    @Output() onClose = new EventEmitter<any>();
    
    componentRef: ComponentRef<any>;

    constructor(
        private cfr: ComponentFactoryResolver
    ) {
    }

    ngOnInit(): void {
        if (this.display) {
            this.createChildComponent();
        }
    }

    createChildComponent() {
        let compFactory = this.cfr.resolveComponentFactory(this.componentType);
        this.componentRef = this.childComponent.createComponent(compFactory);
        this.componentRef.instance.data = this.data;
    }

    closeModal() {
        this.onClose.next();
    }

    ngOnChanges(changes: SimpleChanges) {
        // console.log(JSON.stringify(changes));
    }
    
    ngOnDestroy() {
        this.destroyChildComponent();
    }

    destroyChildComponent() {
        if (this.childComponent) {
            this.childComponent.clear;
        }
        if (this.componentRef) {
            this.componentRef.destroy();
        }
    }
}