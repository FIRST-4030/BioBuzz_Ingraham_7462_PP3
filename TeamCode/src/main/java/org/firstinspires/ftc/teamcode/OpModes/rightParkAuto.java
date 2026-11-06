package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;

import java.util.function.BooleanSupplier;

@Autonomous(name = "Auto for parking on left")
public class rightParkAuto extends OpMode {
    //Components and stuff
    ControlHub controlHub;
    private Follower follower;
    private Collector collector;
    private Limelight limelight;
    private BiobuzzShooter shooter;

    //bank of poses
    Poses poses = new Poses();

    //Path methods
    private Path startToShoot() {
        return line(poses.startPosRight, poses.shootPosRight).linear(poses.startPosRight, poses.shootPosRight);
    }
    private Path shootToPrePark() {
        return curve(poses.shootPosRight, poses.preParkPosRight).linear(poses.shootPosRight, poses.preParkPosRight);
    }
    private Path startToPark(){
        return curve(poses.startPosLeft, poses.parkControlPosLeft, poses.parkPosLeft).linear(poses.startPosLeft, poses.parkPosLeft);
    }
    private Path robotToPark(){
        Pose currentPose = follower.pose();
        return curve(currentPose, poses.of(24, 24), poses.parkPosLeft).linear(currentPose, poses.parkPosLeft);
    }

    /**
     * Set stuff up
     **/
    @Override
    public void init() {
        //initialize components
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        limelight = new Limelight(hardwareMap);
        shooter = new BiobuzzShooter(hardwareMap, limelight);

        //april tag pipeline
        limelight.setPipeline(1);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }

    /**
     * Runs the routine
     **/
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /**
     * Runs every something i honestly don't know
     **/
    @Override
    public void loop() {
        shooter.overridePower();
        collector.setCollector();
        Scheduler.execute();
        follower.update();
    }

    /**
     * Contains the logic for the routine and where it wants to go
     * Important Lambdas:   instant runs a code bite
     *                      follow schedules a path to follow
     *                      wait until waits until a bool is met
     * @return  returns sequential containing the routine
     **/
    public BooleanSupplier isAtVelocity = () -> shooter.atSpeed();
    private Command autoLogic(){
        return sequential(
                instant(() -> {
                    collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
                    shooter.setShooterServos(false);
                    shooter.resetTargetVelocity();
                    shooter.setBackSpinPower(0.8);
                }),
                follow(follower, startToShoot()),
                waitUntil(() ->
                        limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_AUDIENCE) ||
                        limelight.canSeeHiveCell(Limelight.HiveCell.RED_SCORING)
                ),
                instant(() -> {
                        collector.setCollector();
                        shooter.setTargetVelocity(-1700);
                        waitMs(2000);
                }),
                lazy(() -> waitUntil(isAtVelocity)),
                instant(()->shooter.setShooterServos(true)),
                waitMs(5000),
                follow(follower, shootToPrePark())
        );
    }



    /**
     * this method contains the logic for parking automatically
     * Important Lambdas:   deadline runs two or more processes in parallel and stops all processes once one finishes
     *                      lazy doesn't finalize when the code runs instead it finalizes it values when it's ran
     *
     * @return returns a sequential containing logic for the routine
     **/
    private Command autoRoutine(){
        return sequential(
                deadline(
                        waitMs(20000),
                        autoLogic()
                ),
                lazy(() -> follow(follower, robotToPark()))
        );
    }
}