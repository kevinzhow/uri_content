import 'dart:io';
import 'dart:typed_data';

import 'package:uri_content/src/uri_content_schema_handler/uri_schema_handler.dart';

class FileUriHandler implements UriSchemaHandler {
  const FileUriHandler();

  @override
  bool canHandle(Uri uri) {
    return uri.scheme == "file";
  }

  @override
  Future<bool> canFetchContent(
    Uri uri,
    UriSchemaHandlerParams _,
  ) {
    return File.fromUri(uri).exists();
  }

  @override
  Stream<Uint8List> getContentStream(
    Uri uri,
    UriSchemaHandlerParams _,
  ) {
    final file = File.fromUri(uri);
    return file.openRead().map(Uint8List.fromList);
  }

  @override
  Future<int?> getContentLength(Uri uri, UriSchemaHandlerParams params) {
    return File.fromUri(uri).length();
  }

  @override
  Future<Uint8List> getContentRange(
    Uri uri,
    int start,
    int length,
    UriSchemaHandlerParams params,
  ) async {
    final file = File.fromUri(uri);
    final raf = await file.open();
    try {
      await raf.setPosition(start);
      return await raf.read(length);
    } finally {
      await raf.close();
    }
  }
}
