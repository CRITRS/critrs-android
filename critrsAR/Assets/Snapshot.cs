using UnityEngine;
using System.Collections;
using System.IO;

public class Snapshot : MonoBehaviour {
    private readonly string TMP_FILE_NAME = "tmp.png";
	
	// Update is called once per frame
	public void TakePhoto() {
        if (Input.touchCount > 0 && Input.GetTouch(0).tapCount == 1)
        {
            Application.CaptureScreenshot(TMP_FILE_NAME);
            AndroidJavaClass unityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
            AndroidJavaObject activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            activity.Call("finish", Path.Combine(Application.persistentDataPath, TMP_FILE_NAME));
        }
	}
}
