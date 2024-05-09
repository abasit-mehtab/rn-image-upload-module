
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNRnImageUploadModuleSpec.h"

@interface RnImageUploadModule : NSObject <NativeRnImageUploadModuleSpec>
#else
#import <React/RCTBridgeModule.h>

@interface RnImageUploadModule : NSObject <RCTBridgeModule>
#endif

@end
