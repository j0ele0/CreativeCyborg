import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router, NavigationExtras } from '@angular/router';
import { ToastController } from '@ionic/angular';
@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})

export class HomePage {
  rooms:Room[] = [];
  response:any;
  root:string = "https://simon-schnitker-apps.de"
  test:string = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=margarita"
  constructor(private http: HttpClient, private router: Router,private toast: ToastController) {
    setInterval(() => {
      this.requestRooms();
    }, 5 * 1000);
  }
  
  requestRooms(){
    let url = this.root + "/room"
    this.http.get(url).subscribe((response)=>{
      this.rooms = response as Room[];
    });
  }  
  
  getRooms(){
    return this.rooms;
  }
  
  refresh(){
    this.rooms = [];
    this.requestRooms();
    this.presentToast('bottom')
  }

  loadRoom( r:Room){
    let navigationExtras : NavigationExtras = {
      state: {
        room: r
      }
    }
    this.router.navigate(['room'], navigationExtras)
  }

  async presentToast(position: 'top' | 'middle' | 'bottom') {
    const toast = await this.toast.create({
      message: 'lade',
      duration: 1000,
      position: position,
    });

    await toast.present();
  }
}

interface Room{
  "id":number,
  "name":string
}
