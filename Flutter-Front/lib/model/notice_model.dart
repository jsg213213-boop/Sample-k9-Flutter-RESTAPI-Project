class NoticeModel {
  final int? id;
  final String? title;
  final String? content;
  final String? writer;
  final bool? topFixed; // 추가
  final String? regDate;
  final int? viewCount;
  final List<NoticeImageModel>? images; // 추가

  NoticeModel({
    this.id, this.title, this.content, this.writer,
    this.topFixed, this.regDate, this.viewCount, this.images,
  });

  factory NoticeModel.fromJson(Map<String, dynamic> json) {
    return NoticeModel(
      id: json['id'],
      title: json['title'],
      content: json['content'],
      writer: json['writer'],
      topFixed: json['topFixed'],
      regDate: json['regDate'],
      viewCount: json['viewCount'],
      // 이미지 리스트 파싱 로직 추가
      images: json['images'] != null
          ? (json['images'] as List).map((i) => NoticeImageModel.fromJson(i)).toList()
          : null,
    );
  }
}

// 이미지 정보를 위한 서브 모델
class NoticeImageModel {
  final int? id;
  final String? fileName;
  final String? uuid;
  final int? ord;

  NoticeImageModel({this.id, this.fileName, this.uuid, this.ord});

  factory NoticeImageModel.fromJson(Map<String, dynamic> json) {
    return NoticeImageModel(
      id: json['id'],
      fileName: json['fileName'],
      uuid: json['uuid'],
      ord: json['ord'],
    );
  }
}