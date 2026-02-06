import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:uri_content/src/uri_content.dart';
import 'package:uri_content/src/uri_content_schema_handler/uri_schema_handler.dart';

void main() {
  group('UriContent.fromRange', () {
    test('clips oversized handler response to requested length', () async {
      final uriContent = UriContent.internal([_OversizedRangeHandler()]);

      final data = await uriContent.fromRange(Uri.parse('mock://example'), 0, 5);

      expect(data.length, 5);
      expect(data, [0, 1, 2, 3, 4]);
    });

    test('throws for negative length', () async {
      final uriContent = UriContent.internal([_OversizedRangeHandler()]);

      expect(
        () => uriContent.fromRange(Uri.parse('mock://example'), 0, -1),
        throwsRangeError,
      );
    });
  });
}

class _OversizedRangeHandler implements UriSchemaHandler {
  @override
  bool canHandle(Uri uri) => uri.scheme == 'mock';

  @override
  Future<bool> canFetchContent(Uri uri, UriSchemaHandlerParams params) async => true;

  @override
  Stream<Uint8List> getContentStream(Uri uri, UriSchemaHandlerParams params) async* {
    yield Uint8List.fromList(List<int>.generate(10, (i) => i));
  }

  @override
  Future<int?> getContentLength(Uri uri, UriSchemaHandlerParams params) async => 10;

  @override
  Future<Uint8List> getContentRange(
    Uri uri,
    int start,
    int length,
    UriSchemaHandlerParams params,
  ) async {
    return Uint8List.fromList(List<int>.generate(10, (i) => i));
  }
}
