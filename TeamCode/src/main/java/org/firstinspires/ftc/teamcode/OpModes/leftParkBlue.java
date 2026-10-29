package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.behaviors.BlockedBehavior;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.behaviors.InterruptedBehavior;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;
import org.firstinspires.ftc.teamcode.Collector.CollectorMode;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;

@Disabled
@Autonomous(name = "Left Side Park (Blue Team)")
public class leftParkBlue extends OpMode {
    ControlHub controlHub;
    private Collector collector;
    private Limelight limelight;
    private Follower follower;
    private ElapsedTime timer = new ElapsedTime();
    private final Poses poses = new Poses();

    //shooting vars
    private boolean shotBalls = false;
    private boolean skippedShooting = false;
    private Path startToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }
    private Path shootToCollect() {
        return curve(poses.shootPosLeft, poses.collectControlPosLeft, poses.collectPosLeft).linear(poses.shootPosLeft, poses.collectPosLeft);
    }
    private Path collectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }
    private Path shootToPark() {
        return curve(poses.shootPosLeft, poses.parkControlPosLeft, poses.parkPosLeft).linear(poses.shootPosLeft, poses.parkPosLeft);
    }

    private Path startToPark(){
        return curve(poses.startPosLeft, poses.collectPosLeft, poses.parkPosLeft).linear(poses.startPosLeft, poses.parkPosLeft);
    }
    private Path collectToPark(){
        return curve(poses.collectPosLeft, poses.parkPosLeft).linear(poses.collectPosLeft, poses.parkPosLeft);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        limelight = new Limelight(hardwareMap);

        limelight.setPipeline(1);
        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }
    @Override
    public void start() {
        timer.reset();
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    private Command autoRoutine(){
        return sequential(
            follow(follower, startToShoot()),
            shoot(),
            conditional(
                    () -> !skippedShooting,
                    sequential(
                            turnCollectorsOn(CollectorMode.COLLECT),
                            follow(follower, shootToCollect()),
                            follow(follower, collectToShoot()),
                            follow(follower, shootToPark())
                    ),
                    follow(follower, startToPark()) //placeholder
            )
        );
    }

    private Command shoot(){
        ElapsedTime shootBuffer = new ElapsedTime();
        return new Command() {
            @Override
            public Set<Object> requirements() {
                return Collections.emptySet();
            }

            @Override
            public int priority() {
                return 0;
            }

            @Override
            public InterruptedBehavior interruptedBehavior() {
                return null;
            }

            @Override
            public ConflictBehavior conflictBehavior() {
                return null;
            }

            @Override
            public BlockedBehavior blockedBehavior() {
                return null;
            }

            @Override
            public void start() {
                follower.idle();
            }

            @Override
            public boolean done() {
                return shotBalls && shootBuffer.seconds() > 1;
            }

            @Override
            public void execute() {
                if (limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_AUDIENCE)){
                    collector.setCollectorSpeed(CollectorMode.IDLE);
                    shootBuffer.reset();
                    // shoot
                    shotBalls = true;

                } else if (timer.seconds() > 20) {
                    shotBalls = true;
                    skippedShooting = true;
                }
            }

            @Override
            public void end(EndCondition endCondition) {
                collector.setCollectorSpeed(CollectorMode.IDLE);
                shotBalls = false;
            }
        };
    }
    private Command turnCollectorsOn(CollectorMode colSpeed){
        return new Command() {
            @Override
            public Set<Object> requirements() {
                return Collections.emptySet();
            }

            @Override
            public int priority() {
                return 0;
            }

            @Override
            public InterruptedBehavior interruptedBehavior() {
                return null;
            }

            @Override
            public ConflictBehavior conflictBehavior() {
                return null;
            }

            @Override
            public BlockedBehavior blockedBehavior() {
                return null;
            }

            @Override
            public void start() {
                collector.setCollectorSpeed(colSpeed);
            }

            @Override
            public boolean done() {
                return true;
            }

            @Override
            public void execute() {

            }

            @Override
            public void end(EndCondition endCondition) {

            }
        };
    }
}