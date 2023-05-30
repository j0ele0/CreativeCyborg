import { AfterContentInit, AfterViewInit, Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-room',
  templateUrl: 'room.page.html',
  styleUrls: ['room.page.scss'],
})

export class RoomPage {
  ideas:Idea[] = [];
  ideaCount:number = 0;
  room:Room = {id:0,name:""};
  response:any;
  api_url:string = "https://simon-schnitker-apps.de/room/"
  test:string = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=margarita"

  constructor(private http: HttpClient,private route: ActivatedRoute, private router: Router,private toast: ToastController) {
    if (this.router.getCurrentNavigation()?.extras.state) {
      let state:any = this.router.getCurrentNavigation()?.extras.state;
      this.room = state.room;
    }
    this.api_url += this.room.id + "/content";
    setInterval(() => {
      this.getContent();
    }, 5 * 1000);
  }



  private setBeispiel(){
    this.ideas=[{idea:'Test1', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
                {idea:'Test2', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
                {idea:'Test3', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
                {idea:'Test4', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
                {idea:'Test5', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
                {idea:'Test6', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
                {idea:'Test7', imageLink:'https://docs-demo.ionic.io/assets/madison.jpg'},
              ];

  }

  getContent(){
    this.http.get(this.api_url).subscribe((response)=>{
      if(response != null)
      this.ideas = response as Idea[];
    });
    this.ideaCount = this.ideas.length;
  }

  refresh(){
    this.ideas = [];
    this.getContent();
    this.presentToast('bottom')
  }

  getIdeas():Idea[]{
    //this.setBeispiel();
    return this.ideas;
  }
  getIdeaCount():number{
    return this.ideaCount;
  }

  async presentToast(position: 'top' | 'middle' | 'bottom') {
    const toast = await this.toast.create({
      message: 'lade...',
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

interface Idea{
  "idea":string,
  "imageLink": string
}
