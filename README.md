## 대나무꽃
![그래픽 이미지](https://github.com/sghoregooteitehoo03/BambooFlower/blob/master/image/%E1%84%80%E1%85%B3%E1%84%85%E1%85%A2%E1%84%91%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png)

매일매일 다양하고 간단한 미션을 수행하면서 반복되는 무기력한 일상에서 벗어나 활기찬 일상을 시작해보세요!

반복되고, 무기력한 일상.  
**대나무꽃**을 통해 매일 제공되는 미션들을 수행하면서 반복되는 일상의 굴레에서 벗어나 보세요.  
다른 이들에게 수행한 미션을 인증하며, 서로 응원해 보아요.


## 기능
- **메일 제공되는 다양하고 간단한 미션**  
밖에서나 집에서나 어디에서든 수행 가능한 간단한 미션들이 매일매일 제공됩니다.
제공되는 미션을 수행하여 소소한 성취감을 느껴보세요.

- **인증 게시판**  
다른 사람들과 미션을 수행한 모습들을 공유/인증하면서 서로 응원해보세요.

- **하루 일기**  
자유롭게 작성하는 일기를 통해 오늘 하루를 되돌아보는 시간을 가져볼까요?

- **랭킹 시스템**  
누가 더 미션을 많이 수행하였는지 다른 이들과 경쟁해보세요!

## 스크린샷
![스크린샷](https://github.com/sghoregooteitehoo03/BambooFlower/blob/master/image/screenshot.png)

## 아키텍쳐 및 라이브러리
- 아키텍처
   - MVVM 패턴: (View - ViewModel - Model)
   - [App Architecture 패턴](https://developer.android.com/topic/architecture/intro): (UI Layer - Domain Layer - Data Layer)
     
- Jetpack
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.
  - WorkManager: 안정적인 백그라운드 작업을 처리하도록 도와줍니다.
  - Paging3: 로컬 데이터베이스나 네트워크에서 가져온 데이터를 페이징하여 데이터를 처리합니다.
  - Navigation: 화면 구성 및 화면전환에 관련된 다양한 기능을 제공합니다.
  - Browser: 앱 내에서 외부 브라우저를 호출하거나 웹뷰를 제공합니다.
  - Room: SQL 기능을 이용하여 데이터베이스를 이용합니다.
  - Datastore: 키-값 유형의 데이터를 읽고 저장하는 데이터 저장소입니다.
  - Compose: 기존의 XML레이아웃을 이용하지 않고, Kotlin 코드를 통해 UI 화면을 제작합니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입을 통해 보일러플레이트 코드를 줄여줍니다.
    
- [Retrofit](https://github.com/square/retrofit): Android 및 Java를 위한 HTTP 클라이언트입니다.

- [Coil](https://github.com/coil-kt/coil), [Picasso](https://github.com/square/picasso): 네트워크로부터 이미지를 로드합니다.

- Custom Views
  - [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar): Jetapck Compose용 Collapsing Toolbar를 제공합니다.
  - [compose-shimmer](https://github.com/valentinilk/compose-shimmer): Jetpack Compose에 shimmer 효과를 제공합니다.
