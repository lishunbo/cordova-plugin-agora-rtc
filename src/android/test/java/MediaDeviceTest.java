import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;

import org.junit.Test;
import org.webrtc.MediaStreamTrack;
import org.webrtc.VideoTrack;

public class MediaDeviceTest {

    @Test
    public void mediaDevice_MediaStreamConstrants() {

        String test1 = "{\"video\":{\"aspectRatio\":11.01}}";
        String test2 = "{\"video\":{\"aspectRatio\":{\"max\":2000, \"min\":-2000, \"exact\":800, \"ideal\":801}}}";
        String test3 = "{\"video\":{\"aspectRatio\":11.01,\"channelCount\":{\"max\":2000, \"min\":-2000, \"exact\":800, \"ideal\":801}}}";
        String test4 = "{\"video\":{\"autoGainControl\":{ \"exact\":false, \"ideal\":true},\"echoCancellation\":true}}";

        String test = test4;
        MediaStreamConstraints constraints = MediaStreamConstraints.fromJson(test);
        System.out.println(constraints.toString());
    }

    @Test
    public void mediaDevice_MediaStreamTrackWrapper() {
        VideoTrack videoTrack = new VideoTrack(1L);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.cacheMediaStreamTrack("", videoTrack, null);
        System.out.println(wrapper.toString());
        MediaStreamTrackWrapper wrapper1 = MediaStreamTrackWrapper.cacheMediaStreamTrack("", videoTrack, null);
        System.out.println(wrapper1.toString());
        System.out.println(wrapper.toString().equals(wrapper1.toString()));

        MediaStreamTrackWrapper wrapper2 = MediaStreamTrackWrapper.getMediaStreamTrackById(wrapper1.getId());
        assert (wrapper2 != null);
        VideoTrack videoTrack2 = (VideoTrack) wrapper2.getTrack();
        if (videoTrack == wrapper1.getTrack()) {
            System.out.println("have original media stream track");
        } else {
            System.out.println("put but lost original media stream track");
        }
        if (videoTrack2 == videoTrack) {
            System.out.println("have original video track");
        } else {
            System.out.println("put but lost original video track");
        }


    }
}
