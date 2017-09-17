using UnityEngine;
using System.Collections;
using System.IO.Ports;
using System.IO;
using System;
using System.Linq;
using System.Collections.Generic;

[System.Serializable]
public class Done_Boundary 
{
	public float xMin, xMax, zMin, zMax;
}


public class Done_PlayerController : MonoBehaviour
{
	public float speed;
	public float tilt;
	public Done_Boundary boundary;

	public GameObject shot;
	public Transform shotSpawn;
	public float fireRate;
	 
	private float nextFire;



    SerialPort sp = new SerialPort("COM3", 115200);
    float[] xyz_angle = { 0.0f, 0.0f, 0.0f, 0.0f }; //the last entry is the analog input for FIRE

    void Start()
    {
        sp.Open();
        sp.ReadTimeout = 17;
        Debug.Log("Port Setup");

    }


    void Update ()
	{
        if (sp.IsOpen)
        {
            try
            {
                serialEvent(sp);
            }
            catch (System.Exception)
            {
                throw;
            }
        }
        //if (Input.GetButton("Fire1") && Time.time > nextFire)
            if (xyz_angle[3] > 0.9 && Time.time > nextFire)
            {
			nextFire = Time.time + fireRate;
			Instantiate(shot, shotSpawn.position, shotSpawn.rotation);
			GetComponent<AudioSource>().Play ();
		}
	}

	void FixedUpdate ()
	{


        //      float moveHorizontal = Input.GetAxis ("Horizontal");
        //float moveVertical = Input.GetAxis ("Vertical");

        float moveHorizontal = xyz_angle[2]/2;

        if (moveHorizontal > 2)
        {
            moveHorizontal = 2f;
        }
        else if (moveHorizontal<= -2){
            moveHorizontal = -2f;
        };

        float moveVertical = xyz_angle[0]*-1/2;

		Vector3 movement = new Vector3 (moveHorizontal, 0.0f, moveVertical);
		GetComponent<Rigidbody>().velocity = movement * speed;
		
		GetComponent<Rigidbody>().position = new Vector3
		(
			Mathf.Clamp (GetComponent<Rigidbody>().position.x, boundary.xMin, boundary.xMax), 
			0.0f, 
			Mathf.Clamp (GetComponent<Rigidbody>().position.z, boundary.zMin, boundary.zMax)
		);
		
		GetComponent<Rigidbody>().rotation = Quaternion.Euler (0.0f, 0.0f, GetComponent<Rigidbody>().velocity.x * -tilt);
	}

    void serialEvent(SerialPort myPort)
    {

        try
        {
            bool flag = false;
            while (!flag)
            {
                string angles_str = myPort.ReadTo("\n");
                Debug.Log(angles_str);
                string[] angles = angles_str.Split(',');
                try
                {
                    xyz_angle[0] = float.Parse(angles[0]);
                    xyz_angle[1] = float.Parse(angles[1]);
                    xyz_angle[2] = float.Parse(angles[2]);
                    xyz_angle[3] = float.Parse(angles[3]); //FIRE
                    Debug.Log("Parsed: " + xyz_angle[0] + ", " + xyz_angle[1] + ", " + xyz_angle[2] +", " + xyz_angle[3]);
                    flag = true;
                }
                catch(Exception e)
                {
                    Debug.Log(e);
                }
            }

        }
        //catch (TimeoutException e)
        catch (Exception e)
        {
            Debug.Log(e);
        }

    }
}
