using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Renderer))]
public class GetImage : MonoBehaviour {

	// Use this for initialization
    IEnumerator Start() {
        AndroidJavaClass unityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
        AndroidJavaObject activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

        string url = activity.Call<string>("getUrl");

        Renderer renderer = GetComponent<Renderer>();
        renderer.enabled = false;
        WWW remote = new WWW(url);
        yield return remote;
        renderer.material.mainTexture = remote.texture;
        renderer.enabled = true;
	}
}
