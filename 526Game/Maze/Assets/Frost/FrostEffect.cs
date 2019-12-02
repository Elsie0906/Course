using UnityEngine;

[ExecuteInEditMode]
[AddComponentMenu("Image Effects/Frost")]
public class FrostEffect : MonoBehaviour
{
    public float FrostAmount = 0f; //0-1 (0=minimum Frost, 1=maximum frost)
    public int FrostCount = 0;
    public float EdgeSharpness = 2f; //>=1
    public float minFrost = 0.2f; //0-1
    public float maxFrost = 0.6f; //0-1
    public float seethroughness = 0.7f; //blends between 2 ways of applying the frost effect: 0=normal blend mode, 1="overlay" blend mode
    public float distortion = 0.1f; //how much the original image is distorted through the frost (value depends on normal map)
    public Texture2D Frost; //RGBA
    public Texture2D FrostNormals; //normalmap
    public Shader Shader; //ImageBlendEffect.shader
	
	private Material material;

	private void Awake()
	{
        material = new Material(Shader);
        material.SetTexture("_BlendTex", Frost);
        material.SetTexture("_BumpMap", FrostNormals);
	}
	
	private void OnRenderImage(RenderTexture source, RenderTexture destination)
    {
        if (!Application.isPlaying)
        {
            material.SetTexture("_BlendTex", Frost);
            material.SetTexture("_BumpMap", FrostNormals);
            EdgeSharpness = Mathf.Max(1, EdgeSharpness);
        }
        material.SetFloat("_BlendAmount", Mathf.Clamp01(Mathf.Clamp01(FrostAmount) * (maxFrost - minFrost) + minFrost));
        material.SetFloat("_EdgeSharpness", EdgeSharpness);
        material.SetFloat("_SeeThroughness", seethroughness);
        material.SetFloat("_Distortion", distortion);
        //Debug.Log("_Distortion: "+ distortion);

		Graphics.Blit(source, destination, material);
	}

    public void setFrostAmountToStage(int stage) // stage 0-9
    {
        FrostAmount = 0.1f + 0.1f * stage;
        if(stage < 6)
        {
            Debug.Log("S: " + stage);
            seethroughness = 0.1f;
        }
        else
        {
            seethroughness = 0.4f;
        }
        // reset count
        FrostCount = 0;
    }
    public void incrementFrostCount()
    {
        if (FrostCount < 5)
        {
            FrostCount++;
            setFrostAmountByCount();
        }
    }

    private void setFrostAmountByCount()
    {
        FrostAmount = FrostAmount + 0.02f;
        //Debug.Log("F: " + FrostAmount);
    }
}