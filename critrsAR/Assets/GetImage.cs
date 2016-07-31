using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Renderer))]
public class GetImage : MonoBehaviour {

	// Use this for initialization
    IEnumerator Start() {
        Renderer renderer = GetComponent<Renderer>();
        renderer.enabled = false;
        // TODO: Use activity.getIntent().getData() for URL
        WWW remote = new WWW("http://mens.ly/files/koala.jpg");
        yield return remote;
        renderer.material.mainTexture = remote.texture;
        renderer.enabled = true;
	}
}
