using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

public enum State
{
    IDLE,
    WALK,
    CHASE,
    ATTACK,
    RETURN,
    DIE,
}
// TODO:
// 怪物的自动销毁/取消碰撞体积

public class EnemyAI : MonoBehaviour
{
    public GameObject player;
    private State state;
    private Animator animator;
    private Vector3 initialPosition;
    private int distance;
    private Quaternion targetRotation;

    private NavMeshAgent agent;
    public const float DEFEND_RADIUS = 4.0f;
    public const float CHASE_RADIUS = 8.0f;
    public const float ATTACK_RANGE = 2.0f;
    public const float ATTACK_RATE = 4.0f;

    public int health = 100;
    private float timer = 0;

    private float deadTime;
    public float runSpeed = 2.0f;

    public float rotateSpeed = 0.1f;
    public AudioSource audioSource;
    // Start is called before the first frame update
    void Start()
    {
        player = GameObject.Find("Player");
        initialPosition = this.transform.position;
        animator = GetComponent<Animator>();
        agent = GetComponent<NavMeshAgent>();
        state = State.IDLE;
        audioSource = GetComponent<AudioSource>();
        deadTime = 0f;
        
    }

    // Update is called once per frame
    void FixedUpdate()
    {
        // TODO:
        // 1. Add wander state.
        // 2. Add HP System
        switch (state)
        {
            case State.IDLE:
                playerDistanceCheck();
                break;

            case State.CHASE:
                chase();
                break;

            case State.RETURN:
                returnStart();
                break;
            case State.DIE:
                die();
                break;
        }
    }

    void chase() {
        animator.SetBool("isWalking", true);
        // transform.Translate(Vector3.forward * Time.deltaTime * runSpeed);
        // targetRotation = Quaternion.LookRotation(player.transform.position - this.transform.position, Vector3.up);
        // this.transform.rotation = Quaternion.Lerp(this.transform.rotation, targetRotation, rotateSpeed);
        
        agent.SetDestination(player.transform.position);
        agent.Move(transform.TransformDirection(new Vector3(0, 0, runSpeed*Time.deltaTime)));

        chaseRadiusCheck();
    }

    void returnStart() {
        // targetRotation = Quaternion.LookRotation(initialPosition - this.transform.position, Vector3.up);
        // this.transform.rotation = Quaternion.Lerp(this.transform.rotation, targetRotation, rotateSpeed);
        // this.transform.Translate(Vector3.forward * Time.deltaTime * runSpeed);

        agent.SetDestination(initialPosition);
        agent.Move(transform.TransformDirection(new Vector3(0, 0, runSpeed * Time.deltaTime)));

        returnBackCheck();
    }

    void die() {
        animator.SetBool("isEnemyDead", true);
        
        // deadTime += Time.deltaTime;
        // if (deadTime > 5) {
        //     Destroy(this);
        // }
    }

    void playerDistanceCheck() {
        float distanceToPlayer = Vector3.Distance(player.transform.position, this.transform.position);
        if (distanceToPlayer < ATTACK_RANGE) {
            animator.SetTrigger("standHit");
        }
        if (distanceToPlayer < DEFEND_RADIUS && distanceToPlayer >= ATTACK_RANGE) {
            state = State.CHASE;
        }
    }
    void chaseRadiusCheck() {
        float distanceToPlayer = Vector3.Distance(player.transform.position, this.transform.position);
        float distanceToStart = Vector3.Distance(this.transform.position, this.initialPosition);

        // TODO:
        // 1. debounce 
       if (distanceToPlayer <= ATTACK_RANGE) {
            attack();
        }

        // if enemy distance larget than chase raidus, enemy return back
        if (distanceToStart > CHASE_RADIUS) {
            state = State.RETURN;
        }
    }

    void returnBackCheck() {
        float distanceToStart = Vector3.Distance(this.transform.position, initialPosition);
        if (distanceToStart < 0.1f) {
            animator.SetBool("isWalking", false);
            state = State.IDLE;
        }
    }

    private void attack() {
        Collider[] nearByObject = Physics.OverlapSphere(transform.position, ATTACK_RANGE);
        foreach (Collider obj in nearByObject)
        {
            if ("Player" == obj.gameObject.tag) {
                // player is in the attack range
                timer += Time.deltaTime;
                if (timer > ATTACK_RATE) {
                    obj.GetComponent<PlayerController>().loseHP(10);
                    animator.SetTrigger("hit");
                    audioSource.Play();
                    timer = 0f;
                }
            }
        }
    }

    public void loseHP(int damage) {
        this.health -= damage;
        print(health);
        if (this.health <= 0) {
            this.state = State.DIE;
        }
    }
}
