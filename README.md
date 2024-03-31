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
     
- Jetpack
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.
  - LiveData: Data의 변경을 관찰 할 수 있는 Data Holder 입니다.
  - Paging3: 로컬 데이터베이스나 네트워크에서 가져온 데이터를 페이징하여 데이터를 처리합니다.
  - Navigation: 화면 구성 및 화면전환에 관련된 다양한 기능을 제공합니다.
  - Room: SQL 기능을 이용하여 데이터베이스를 이용합니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입을 통해 보일러플레이트 코드를 줄여줍니다.

- Firebase
   - Authentication: 구글 및 다양한 계정으로 로그인 할 수 있는 기능을 제공합니다.
   - Firestore: NoSql 기반의 클라우드 데이터베이스를 이용할 수 있는 기능을 제공합니다.
   - Cloud Functions: Firebase 기능과 HTTPS 요청에 의해 트리거되는 이벤트에 응답하여 백엔드 코드를 자동으로 실행할 수 있는 서버리스 프레임워크입니다.

- [Retrofit](https://github.com/square/retrofit): Android 및 Java를 위한 HTTP 클라이언트입니다.

- [Glide](https://github.com/bumptech/glide): 네트워크로부터 이미지를 로드합니다.

- Custom Views
  - [CircleImageView](https://github.com/hdodenhof/CircleImageView): 원형의 ImageView 입니다.
  - [Android-SpinKit](https://github.com/ybq/Android-SpinKit): 다양한 로딩 애니메이션을 제공합니다.
  - [PhotoView](https://github.com/Baseflow/PhotoView): 확대/축소 가능한 ImageView를 제공합니다.
  - [shimmer-android](https://github.com/facebookarchive/shimmer-android): View에 shimmer 효과를 추가합니다.

## SDK
### [Facebook SDK](https://developers.facebook.com/docs?locale=ko_KR)
