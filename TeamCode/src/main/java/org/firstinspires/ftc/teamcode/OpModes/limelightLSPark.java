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
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.generalUtilities.Blackboard;
import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;

import java.util.List;
import java.util.function.BooleanSupplier;

@Disabled
@Autonomous(name = "xxxLSP")
public class limelightLSPark extends OpMode {

    //Components and stuff
    ControlHub controlHub;
    private Follower follower;
    private Collector collector;
    private Limelight3A limelight3A;
    private Limelight limelight;
    private BiobuzzShooter shooter;
    private LLResult internalLatestResult;
    private LLStatus internalLatestStatus;

    //bank of poses
    Poses poses = new Poses();

    //Path methods
    private Path startToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }
    private Path collectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }
    private Path shootToPreCollect(){
        return line(poses.shootPosLeft, poses.preCollectPoseLeft).facingPoint(poses.collectPosLeft);
    }
    private Path preCollectToCollect(){
        return line(poses.preCollectPoseLeft, poses.collectPosLeft).facingPoint(poses.collectPosLeft);
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
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
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

    @Override
    public void init_loop() {
        if (gamepad1.right_bumper) {
            if (gamepad1.bWasPressed()) {
                Blackboard.setAlliance(Blackboard.Alliance.RED);
            } else if (gamepad1.xWasPressed()) {
                Blackboard.setAlliance(Blackboard.Alliance.BLUE);
            }
        }

        telemetry.addLine("[RB + X: BLUE ALLIANCE]");
        telemetry.addLine("[RB + B: RED ALLIANCE]");
        telemetry.addData("Alliance", Blackboard.getAlliance());
    }

    /**
     * Runs the routine
     **/
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /**
     * Runs every something
     **/
    @Override
    public void loop() {
        shooter.overridePower();
        collector.setCollector();
        limelight.updateData();
        Scheduler.execute();
        follower.update();
        internalLatestResult = limelight3A.getLatestResult();
        internalLatestStatus = limelight3A.getStatus();
        telemetry.addData("can see hive", limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_SCORING));
        telemetry.addData("can see other hive", limelight.canSeeHiveCell(Limelight.HiveCell.RED_AUDIENCE));
        telemetry.addData("shooter at speed", shooter.atSpeed());
    }

    public boolean seesHiveCell(Limelight.HiveCell hiveCell) {
        boolean canSeeHiveCell = false;

        List<LLResultTypes.FiducialResult> fiducials = internalLatestResult.getFiducialResults();
        if (fiducials == null) return false;
        for (LLResultTypes.FiducialResult fiducial : fiducials) {
            if (getHiveCellFromTagID(fiducial.getFiducialId()) == hiveCell) {
                canSeeHiveCell = true;
                break;
            }
        }

        return canSeeHiveCell;
    }

    public Limelight.HiveCell getHiveCellFromTagID(int tagID) {
        switch (tagID) {
            case 30:
            case 31:
            case 32:
            case 33:
                return Limelight.HiveCell.RED_SCORING;
            case 34:
            case 35:
            case 36:
            case 37:
                return Limelight.HiveCell.RED_AUDIENCE;
            case 38:
            case 39:
            case 40:
            case 41:
                return Limelight.HiveCell.BLUE_AUDIENCE;
            case 42:
            case 43:
            case 44:
            case 45:
                return Limelight.HiveCell.BLUE_SCORING;
            default:
                return Limelight.HiveCell.NONE;
        }
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
                        seesHiveCell(Limelight.HiveCell.RED_AUDIENCE)||
                        seesHiveCell(Limelight.HiveCell.BLUE_AUDIENCE)
//                        limelight.canSeeHiveCell(Limelight.HiveCell.RED_AUDIENCE) ||
//                        limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_SCORING)
                ),
                instant(() -> shooter.setTargetVelocity(-1480)),
                lazy(() -> waitUntil(isAtVelocity)),
                waitMs(2000),
                instant(() -> shooter.setShooterServos(true)),
                waitMs(3000),
                follow(follower, shootToPreCollect()),
                instant(() -> shooter.setShooterServos(false)),
                follow(follower, preCollectToCollect()),
                follow(follower, collectToShoot()),
                follow(follower, startToShoot()),
                waitUntil(() ->
                        seesHiveCell(Limelight.HiveCell.RED_AUDIENCE)||
                        seesHiveCell(Limelight.HiveCell.BLUE_AUDIENCE)
                ),
                lazy(() -> waitUntil(isAtVelocity)),
                instant(()->shooter.setShooterServos(true)),
                waitMs(5000),
                instant(()->shooter.setShooterServos(false))
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
                instant(()-> collector.setCollectorSpeed(Collector.CollectorMode.IDLE)),
                lazy(() -> follow(follower, robotToPark()))
        );
    }
}